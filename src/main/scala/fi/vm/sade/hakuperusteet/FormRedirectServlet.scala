package fi.vm.sade.hakuperusteet

import java.net.URLEncoder
import java.text.SimpleDateFormat

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{User, PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._


class FormRedirectServlet(config: Config, db: HakuperusteetDatabase, signer: RSASigner) extends HakuperusteetServlet(config, db) {

  get("/redirect") {
    failUnlessAuthenticated

    val host = config.getString("form.redirect.base")
    val userData = userDataFromSession
    val payments = db.findPayments(userData)
    val shouldPay = userData.educationCountry != "Finland"
    val hasPaid = payments.exists((p: Payment) => p.status.equals(PaymentStatus.ok))
    compact(render(Map("url" -> generateUrl(host, userData, shouldPay, hasPaid))))
  }

  private def generateUrl(host: String, userData: User, shouldPay: Boolean, hasPaid: Boolean) = {
    val seq = paramSequence(userData, shouldPay, hasPaid)
    val signature = signer.signData(seq.map(_._2).mkString(""))
    val query = seq.map{ case (k, v) => s"$k=${URLEncoder.encode(v, "UTF-8")}" }.mkString("&") + s"&signature=$signature"
    s"$host?$query"
  }

  def paramSequence(u: User, shouldPay: Boolean, hasPaid: Boolean) =
    Seq(("personOid", u.personOid.getOrElse(halt(500))), ("email", u.email), ("firstName", u.firstName), ("lastName", u.lastName),
      ("birthDate", new SimpleDateFormat("ddMMyyyy").format(u.birthDate)), ("personId", u.personId.getOrElse("")),
      ("gender", u.gender), ("nationality", u.nationality), ("educationLevel", u.educationLevel),
      ("educationCountry", u.educationCountry), ("shouldPay", shouldPay.toString), ("hasPaid", hasPaid.toString))
}
