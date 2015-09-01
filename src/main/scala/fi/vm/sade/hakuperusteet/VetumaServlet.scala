package fi.vm.sade.hakuperusteet

import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.{PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.vetuma.{Vetuma, VetumaUrl}
import org.joda.time.DateTime

class VetumaServlet(config: Config, db: HakuperusteetDatabase) extends HakuperusteetServlet(config, db) {

  before() {
    contentType = "application/json"
  }

  get("/openvetuma") {
    failUnlessAuthenticated

    val language = "fi"
    val ref = "1234561"
    val orderNro = Vetuma.generateOrderNumber

    val payment = Payment(user.personOid.get, new Date(), ref, orderNro, PaymentStatus.started)
    db.insertPayment(user, payment)

    Vetuma(config, payment, language).toUrl
  }

  post("/return/ok") {
    val url = config.getString("host.url.base") + "?result=ok"
    handleReturn(url, PaymentStatus.ok)
  }

  post("/return/cancel") {
    val url = config.getString("host.url.base") + "?result=cancel"
    handleReturn(url, PaymentStatus.cancel)
  }

  post("/return/error") {
    val url = config.getString("host.url.base") + "?result=error"
    handleReturn(url, PaymentStatus.error)
  }

  private def handleReturn(url: Oid, status: PaymentStatus) {
    db.findPayment(user) match {
      case Some(p) =>
        val paymentOk = p.copy(status = status)
        db.updatePayment(paymentOk)
        halt(status = 303, headers = Map("Location" -> url.toString))
      case None =>
        // todo: handle this
        halt(status = 303, headers = Map("Location" -> url.toString))
    }
  }
}
