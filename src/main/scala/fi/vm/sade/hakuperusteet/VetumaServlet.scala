package fi.vm.sade.hakuperusteet

import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.{User, PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.email.{ReceiptValues, EmailTemplate, EmailSender}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.vetuma.Vetuma

class VetumaServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, emailSender: EmailSender) extends HakuperusteetServlet(config, db, oppijanTunnistus) {

  get("/openvetuma") {
    failUnlessAuthenticated

    val userData = userDataFromSession
    val language = "en"
    val referenceNumber = referenceNumberFromPersonOid(userData.personOid.getOrElse(halt(500)))
    val orderNro = referenceNumber + db.nextOrderNumber()
    val paymCallId = "PCID" + orderNro
    val payment = Payment(None, userData.personOid.get, new Date(), referenceNumber, orderNro, paymCallId, PaymentStatus.started)
    val paymentWithId = db.upsertPayment(payment).getOrElse(halt(500))
    Vetuma(config, paymentWithId, language).toUrl
  }

  post("/return/ok") {
    val url = config.getString("host.url.base") + "#/effect/VetumaResultOk"
    handleReturn(url, PaymentStatus.ok)
  }

  post("/return/cancel") {
    val url = config.getString("host.url.base") + "#/effect/VetumaResultCancel"
    handleReturn(url, PaymentStatus.cancel)
  }

  post("/return/error") {
    val url = config.getString("host.url.base") + "#/effect/VetumaResultError"
    handleReturn(url, PaymentStatus.error)
  }

  def referenceNumberFromPersonOid(personOid: String) = personOid.split("\\.").toList.last

  private def handleReturn(url: Oid, status: PaymentStatus) {
    val macParams = createMacParams
    val expectedMac = params.getOrElse("MAC", "")
    if (!Vetuma.verifyReturnMac(config.getString("vetuma.shared.secret"), macParams, expectedMac)) halt(409)

    val userData = userDataFromSession
    db.findPaymentByOrderNumber(userData, params.getOrElse("ORDNR", "")) match {
      case Some(p) =>
        val paymentOk = p.copy(status = status)
        db.upsertPayment(paymentOk)
        if (status == PaymentStatus.ok) {
          sendReceipt(userData, paymentOk)
        }
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

  private def sendReceipt(userData: User, payment: Payment): Unit = {
    val p = ReceiptValues("12.12.2015", "100", payment.reference)
    emailSender.send(userData.email, "Payment receipt", EmailTemplate.renderReceipt(p))
  }
}
