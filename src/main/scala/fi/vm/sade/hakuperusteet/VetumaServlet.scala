package fi.vm.sade.hakuperusteet

import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.{PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.vetuma.Vetuma

class VetumaServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus) extends HakuperusteetServlet(config, db, oppijanTunnistus) {

  get("/openvetuma") {
    failUnlessAuthenticated

    val language = "en"
    val ref = "1234561"
    val orderNro = Vetuma.generateOrderNumber

    val userData = userDataFromSession

    val payment = Payment(None, userData.personOid.get, new Date(), ref, orderNro, PaymentStatus.started)
    val paymentWithId = db.upsertPayment(payment).getOrElse(halt(500))
    Vetuma(config, paymentWithId, language).toUrl
  }

  post("/return/ok") {
    val url = config.getString("host.url.base") + "#VetumaResultOk"
    handleReturn(url, PaymentStatus.ok)
  }

  post("/return/cancel") {
    val url = config.getString("host.url.base") + "#VetumaResultCancel"
    handleReturn(url, PaymentStatus.cancel)
  }

  post("/return/error") {
    val url = config.getString("host.url.base") + "#VetumaResultError"
    handleReturn(url, PaymentStatus.error)
  }

  private def handleReturn(url: Oid, status: PaymentStatus) {
    val macParams = createMacParams
    val expectedMac = params.getOrElse("MAC", "")
    if (!Vetuma.verifyReturnMac(config.getString("vetuma.shared.secret"), macParams, expectedMac)) halt(409)

    db.findPaymentByOrderNumber(userDataFromSession, params.getOrElse("ORDNR", "")) match {
      case Some(p) =>
        val paymentOk = p.copy(status = status)
        db.upsertPayment(paymentOk)
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
