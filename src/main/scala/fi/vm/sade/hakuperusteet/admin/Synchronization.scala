package fi.vm.sade.hakuperusteet.admin

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit._

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.db.generated.Tables
import fi.vm.sade.hakuperusteet.domain.PaymentState.PaymentState
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.PaymentUpdate
import fi.vm.sade.hakuperusteet.domain._
import fi.vm.sade.hakuperusteet.hakuapp.HakuAppClient
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.{ApplicationSystem, Tarjonta}
import fi.vm.sade.hakuperusteet.redirect.RedirectCreator._
import fi.vm.sade.hakuperusteet.util.PaymentUtil
import org.apache.http.HttpVersion
import org.apache.http.client.fluent.{Response, Request}
import org.apache.http.entity.ContentType
import org.http4s
import scala.util.control.Exception._
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}

class Synchronization(config: Config, db: HakuperusteetDatabase, tarjonta: Tarjonta, countries: Countries, signer: RSASigner) extends LazyLogging {
  import fi.vm.sade.hakuperusteet._
  val hakuAppClient = HakuAppClient.init(config)
  val scheduler = Executors.newScheduledThreadPool(1)
  def start = scheduler.scheduleWithFixedDelay(checkTodoSynchronizations, 1, config.getDuration("admin.synchronization.interval", SECONDS), SECONDS)

  def checkTodoSynchronizations = asSimpleRunnable { () =>
    db.fetchNextSyncIds.foreach(checkSynchronizationForId)
  }

  protected def checkSynchronizationForId(id: Int): Unit = {
    db.findSynchronizationRequest(id).foreach(syncs => syncs match {
      case Some(sync: HakuAppSyncRequest) => synchronizePaymentRow(sync)
      case Some(sync: ApplicationObjectSyncRequest) => synchronizeUserRow(sync)
      case _ => logger.error("Unexpected sync request")
    })
  }

  private def synchronizePaymentRow(row: HakuAppSyncRequest) = {
    val payments = PaymentUtil.sortPaymentsByStatus(db.findUserByOid(row.henkiloOid).map(db.findPayments(_)).getOrElse(Seq())).headOption
    (payments match {
      case Some(payment) => paymentStatusToPaymentState(payment.status)
      case _ => None
    }) match {
      case Some(state) => Try {
        logger.info(s"Synching row id ${row.id} with Haku-App, matching fake operation: " + createCurl(hakuAppClient.url(row.hakemusOid), write(PaymentUpdate(state))))
        hakuAppClient.updateHakemusWithPaymentState(row.hakemusOid, state) } match {
        case Success(r) => handleHakuAppPostSuccess(row, state, r)
        case Failure(f) => handleSyncError(row.id, "Synchronization to Haku-App throws", f)
      }
      case None => {
        // TODO: What to do when payment has no state that requires update?
        db.markSyncDone(row.id)
      }
    }
  }

  private def paymentStatusToPaymentState(status: PaymentStatus) =
  status match {
    case PaymentStatus.ok => Some(PaymentState.OK)
    case PaymentStatus.cancel => Some(PaymentState.NOTIFIED)
    case PaymentStatus.error => Some(PaymentState.NOT_OK)
    case _ => None
  }

  private def handleHakuAppPostSuccess(row: HakuAppSyncRequest, state: PaymentState, response: http4s.Response): Unit = {
    val statusCode = response.status.code
    statusCode match {
      case 200 | 204 =>
        logger.info(s"Synced row id ${row.id} with Haku-App, henkiloOid ${row.henkiloOid}, hakemusOid ${row.hakemusOid} and payment state ${state}")
        db.markSyncDone(row.id)
      case 403 =>
        logger.warn(s"Tried to sync row id ${row.id} with Haku-App, henkiloOid ${row.henkiloOid}, hakemusOid ${row.hakemusOid} and payment state ${state} but the user is no longer liable for payment.")
        db.markSyncDone(row.id)
      case _ =>
        logger.error(s"Synchronization error with statuscode $statusCode")
        db.markSyncError(row.id)
    }
  }

  protected def synchronizeUserRow(row: ApplicationObjectSyncRequest) =
    Try { tarjonta.getApplicationSystem(row.hakuOid) } match {
      case Success(as) => continueWithTarjontaData(row, as)
      case Failure(f) => handleSyncError(row.id, "Synchronization Tarjonta application system throws", f)
    }

  private def continueWithTarjontaData(row: ApplicationObjectSyncRequest, as: ApplicationSystem) =
    db.findUserByOid(row.henkiloOid).foreach { (u) =>
      (u) match {
        case u:User =>
          db.findApplicationObjectByHakukohdeOid(u, row.hakukohdeOid)
            .foreach(synchronizeWithData(row, as, u, db.findPayments(u)))
        case u: PartialUser =>
          logger.error("PartialUser in user sync loop!")
      }
    }

  private def synchronizeWithData(row: ApplicationObjectSyncRequest, as: ApplicationSystem, u: User, payments: Seq[Payment])(ao: ApplicationObject) {
    val shouldPay = countries.shouldPay(ao.educationCountry, ao.educationLevel)
    val hasPaid = payments.exists(_.status.equals(PaymentStatus.ok))
    val formUrl = as.formUrl
    val body = generatePostBody(generateParamMap(signer, u, ao, shouldPay, hasPaid, admin = true))
    logger.info(s"Synching row id ${row.id}, matching fake operation: " + createCurl(formUrl, body))
    Try { doPost(formUrl, body) } match {
      case Success(response) => handlePostSuccess(row, response)
      case Failure(f) => handleSyncError(row.id, "Synchronization POST throws", f)
    }
  }

  private def handlePostSuccess(row: ApplicationObjectSyncRequest, response: Response): Unit = {
    val statusCode = response.returnResponse().getStatusLine.getStatusCode
    if (statusCode == 200 || statusCode == 204) {
      logger.info(s"Synced row id ${row.id}, henkiloOid ${row.henkiloOid}, hakukohdeoid ${row.hakukohdeOid}")
      db.markSyncDone(row.id)
    } else {
      logger.error(s"Synchronization error with statuscode $statusCode, message was " + allCatch.opt(response.returnContent().asString()))
      db.markSyncError(row.id)
    }
  }

  private def doPost(formUrl: String, body: String) = Request.Post(formUrl).useExpectContinue().version(HttpVersion.HTTP_1_1)
    .bodyString(body, ContentType.create("application/x-www-form-urlencoded")).execute()

  private def createCurl(formUrl: String, body: String) = "curl -i -X POST --data \"" + body + "\" " + formUrl

  private def handleSyncError(id: Int, errorMsg: String, f: Throwable) = {
    db.markSyncError(id)
    logger.error(errorMsg, f)
  }

  private def asSimpleRunnable(f: () => Unit) = new Runnable() { override def run() { f() } }
}

object Synchronization {
  def apply(config: Config, db: HakuperusteetDatabase, tarjonta: Tarjonta, countries: Countries, signer: RSASigner) = new Synchronization(config, db, tarjonta, countries, signer)
}

object SynchronizationStatus extends Enumeration {
  type SynchronizationStatus = Value
  val todo, active, done, error = Value
}
