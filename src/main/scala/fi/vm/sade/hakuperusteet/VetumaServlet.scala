package fi.vm.sade.hakuperusteet

import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.{PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.vetuma.Vetuma

class VetumaServlet(config: Config, db: HakuperusteetDatabase) extends HakuperusteetServlet(config, db) {

  get("/openvetuma") {
    failUnlessAuthenticated

    val language = "fi"
    val ref = "1234561"
    val orderNro = Vetuma.generateOrderNumber

    val userData = userDataFromSession

    val payment = Payment(None, userData.personOid.get, new Date(), ref, orderNro, PaymentStatus.started)
    db.insertPayment(userData, payment)

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
    val macParams = createMacParams
    val expectedMac = params.getOrElse("MAC", "")
    if (!Vetuma.verifyReturnMac(config.getString("vetuma.shared.secret"), macParams, expectedMac)) halt(409)

    db.findPayment(userDataFromSession) match {
      case Some(p) =>
        val paymentOk = p.copy(status = status)
        db.updatePayment(paymentOk)
        halt(status = 303, headers = Map("Location" -> url.toString))
      case None =>
        // todo: handle this
        halt(status = 303, headers = Map("Location" -> url.toString))
    }
  }

  private def createMacParams = {
    def p(name: String) = params.getOrElse(name, "")
    List(p("RCVID"), p("TIMESTMP"), p("SO"), p("LG"), p("RETURL"), p("CANURL"), p("ERRURL"), p("PAYID"), p("REF"), p("ORDNR"), p("PAID"), p("STATUS"))
  }
}
