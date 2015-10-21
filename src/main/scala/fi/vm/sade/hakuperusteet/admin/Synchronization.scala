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
    val body = generatePostBody(generateParamMap(signer, u, ao, shouldPay, hasPaid))
    println(formUrl + ":" + body)

    db.markSyncDone(row)
  }

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
