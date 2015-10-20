package fi.vm.sade.hakuperusteet.admin

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit._

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.PaymentStatus
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import fi.vm.sade.hakuperusteet.redirect.RedirectCreator._

import scala.util.{Failure, Success, Try}

class Synchronization(config: Config, db: HakuperusteetDatabase, tarjonta: Tarjonta, countries: Countries, signer: RSASigner) extends LazyLogging {
  val scheduler = Executors.newScheduledThreadPool(1)

  scheduler.scheduleWithFixedDelay(checkTodoSynchronizations, 1, config.getDuration("admin.synchronization.interval", SECONDS), SECONDS)

  def checkTodoSynchronizations = asSimpleRunnable { () =>
    db.fetchNextSyncIds.foreach( (id) => {
      db.findSynchronizationRow(id).foreach( (row) => {
        Try { tarjonta.getApplicationSystem(row.hakuOid) } match {
          case Success(as) =>
            db.findUserByOid(row.henkiloOid) match {
              case Some(u) =>
                val payments = db.findPayments(u)
                db.findApplicationObjectByHakukohdeOid(u, row.hakukohdeOid) match {
                  case Some(ao) =>
                    val shouldPay = countries.shouldPay(ao.educationCountry)
                    val hasPaid = payments.exists(_.status.equals(PaymentStatus.ok))
                    val formUrl = as.formUrl
                    val body = generatePostBody(generateParamMap(signer, u, ao, shouldPay, hasPaid))
                    println(formUrl + ":" + body)
                  case _ => logger.error("Synchronization ao not found!")
                }
              case _ => logger.error("Synchronization userData not found!")
            }
          case Failure(f) => logger.error("Synchronization Tarjonta application system throws", f)
        }
      })
    })
  }

  private def asSimpleRunnable(f: () => Unit) = new Runnable() { override def run() { f() } }
}

object Synchronization {
  def apply(config: Config, db: HakuperusteetDatabase, tarjonta: Tarjonta, countries: Countries, signer: RSASigner) = new Synchronization(config, db, tarjonta, countries, signer)
}