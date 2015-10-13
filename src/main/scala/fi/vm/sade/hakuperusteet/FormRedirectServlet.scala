package fi.vm.sade.hakuperusteet

import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, User, PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}


class FormRedirectServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, verifier: GoogleVerifier, signer: RSASigner, countries: Countries) extends HakuperusteetServlet(config, db, oppijanTunnistus, verifier) {

  get("/redirect") {
    failUnlessAuthenticated

    val userData = userDataFromSession
    val hakukohdeOid = params.get("hakukohdeOid").getOrElse(halt(409))
    val applicationObjectForThisHakukohde = db.findApplicationObjectByHakukohdeOid(userDataFromSession, hakukohdeOid).getOrElse(halt(409))
    val host = "FIXME"
    val payments = db.findPayments(userData)
    val shouldPay = countries.shouldPay(applicationObjectForThisHakukohde.educationCountry)
    val hasPaid = payments.exists(_.status.equals(PaymentStatus.ok))
    write(Map("url" -> host, "params" -> generateParamMap(userData, applicationObjectForThisHakukohde, shouldPay, hasPaid)))
  }

  def generateParamMap(userData: User, educationForThisHakukohde: ApplicationObject, shouldPay: Boolean, hasPaid: Boolean) = {
    val seq = paramSequence(userData, shouldPay, hasPaid, educationForThisHakukohde)
    val signature = signer.signData(seq.map(_._2).mkString(""))
    (seq.toList ++ List(("signature", signature))).toMap
  }

  def paramSequence(u: User, shouldPay: Boolean, hasPaid: Boolean, e: ApplicationObject) =
    Seq(("personOid", u.personOid.getOrElse(halt(500))), ("email", u.email), ("firstName", u.firstName), ("lastName", u.lastName),
      ("birthDate", new SimpleDateFormat("ddMMyyyy").format(u.birthDate)), ("personId", u.personId.getOrElse("")),
      ("gender", u.gender), ("nationality", u.nationality), ("hakukohdeOid", e.hakukohdeOid), ("educationLevel", e.educationLevel),
      ("educationCountry", e.educationCountry), ("shouldPay", shouldPay.toString), ("hasPaid", hasPaid.toString),
      ("created", new Date().toInstant.getEpochSecond.toString))

  private def hostBaseUrl(formId: String) = config.getConfig("form.redirect.base").getString(formId)
}
