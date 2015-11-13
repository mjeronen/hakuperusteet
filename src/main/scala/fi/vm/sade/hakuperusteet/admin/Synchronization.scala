package fi.vm.sade.hakuperusteet.admin

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit._

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.db.generated.Tables
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, Payment, User, PaymentStatus}
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.{ApplicationSystem, Tarjonta}
import fi.vm.sade.hakuperusteet.redirect.RedirectCreator._
import org.apache.http.HttpVersion
import org.apache.http.client.fluent.{Response, Request}
import org.apache.http.entity.ContentType
import scala.util.control.Exception._

import scala.util.{Failure, Success, Try}

class Synchronization(config: Config, db: HakuperusteetDatabase, tarjonta: Tarjonta, countries: Countries, signer: RSASigner) extends LazyLogging {
  val scheduler = Executors.newScheduledThreadPool(1)
  scheduler.scheduleWithFixedDelay(checkTodoSynchronizations, 1, config.getDuration("admin.synchronization.interval", SECONDS), SECONDS)

  def checkTodoSynchronizations = asSimpleRunnable { () => db.fetchNextSyncIds.foreach(db.findSynchronizationRow(_).foreach(synchronizeRow)) }

  private def synchronizeRow(row: Tables.SynchronizationRow) =
    Try { tarjonta.getApplicationSystem(row.hakuOid) } match {
      case Success(as) => continueWithTarjontaData(row, as)
      case Failure(f) => handleSyncError(row, "Synchronization Tarjonta application system throws", f)
    }

  private def continueWithTarjontaData(row: Tables.SynchronizationRow, as: ApplicationSystem) =
    db.findUserByOid(row.henkiloOid).foreach { (u) =>
      db.findApplicationObjectByHakukohdeOid(u, row.hakukohdeOid)
        .foreach(synchronizeWithData(row, as, u, db.findPayments(u)))
    }

  private def synchronizeWithData(row: Tables.SynchronizationRow, as: ApplicationSystem, u: User, payments: Seq[Payment])(ao: ApplicationObject) {
    val shouldPay = countries.shouldPay(ao.educationCountry)
    val hasPaid = payments.exists(_.status.equals(PaymentStatus.ok))
    val formUrl = as.formUrl
    val body = generatePostBody(generateParamMap(signer, u, ao, shouldPay, hasPaid, admin = true))
    logger.info(s"Synching row id ${row.id}, matching fake operation: " + createCurl(formUrl, body))
    Try { doPost(formUrl, body) } match {
      case Success(response) => handlePostSuccess(row, response)
      case Failure(f) => handleSyncError(row, "Synchronization POST throws", f)
    }
  }

  private def handlePostSuccess(row: Tables.SynchronizationRow, response: Response): Unit = {
    val statusCode = response.returnResponse().getStatusLine.getStatusCode
    if (statusCode == 200) {
      logger.info(s"Synced row id ${row.id}, henkiloOid ${row.henkiloOid}, hakukohdeoid ${row.hakukohdeOid}")
      db.markSyncDone(row)
    } else {
      logger.error(s"Synchronization error with statuscode $statusCode, message was " + allCatch.opt(response.returnContent().asString()))
      db.markSyncError(row)
    }
  }

  private def doPost(formUrl: String, body: String) = Request.Post(formUrl).useExpectContinue().version(HttpVersion.HTTP_1_1)
    .bodyString(body, ContentType.create("application/x-www-form-urlencoded")).execute()

  private def createCurl(formUrl: String, body: String) = "curl -i -X POST --data \"" + body + "\" " + formUrl

  private def handleSyncError(row: Tables.SynchronizationRow, errorMsg: String, f: Throwable) = {
    db.markSyncError(row)
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
