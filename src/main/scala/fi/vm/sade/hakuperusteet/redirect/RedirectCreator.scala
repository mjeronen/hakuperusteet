package fi.vm.sade.hakuperusteet.redirect

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date

import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, User}
import fi.vm.sade.hakuperusteet.rsa.RSASigner

object RedirectCreator {

  def generateParamMap(signer: RSASigner, userData: User, educationForThisHakukohde: ApplicationObject, shouldPay: Boolean, hasPaid: Boolean) = {
    val seq = paramSequence(userData, shouldPay, hasPaid, educationForThisHakukohde)
    val signature = signer.signData(seq.map(_._2).mkString(""))
    (seq.toList ++ List(("signature", signature))).toMap
  }

  def paramSequence(u: User, shouldPay: Boolean, hasPaid: Boolean, e: ApplicationObject) =
    Seq(("personOid", u.personOid.get), ("email", u.email), ("firstName", u.firstName), ("lastName", u.lastName),
      ("birthDate", new SimpleDateFormat("ddMMyyyy").format(u.birthDate)), ("personId", u.personId.getOrElse("")),
      ("gender", u.gender), ("nationality", u.nationality), ("hakukohdeOid", e.hakukohdeOid), ("educationLevel", e.educationLevel),
      ("educationCountry", e.educationCountry), ("shouldPay", shouldPay.toString), ("hasPaid", hasPaid.toString),
      ("created", new Date().toInstant.getEpochSecond.toString))

  def generatePostBody(params: Map[String, String]) = params.map { case (k, v) => k + "=" + URLEncoder.encode(v, "UTF-8") }.mkString("&")
}
