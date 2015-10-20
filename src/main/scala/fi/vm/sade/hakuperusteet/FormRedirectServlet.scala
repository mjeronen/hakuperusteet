package fi.vm.sade.hakuperusteet

import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, User, PaymentStatus, Payment}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.redirect.RedirectCreator
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.{ApplicationSystem, Tarjonta}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

import scala.util.{Try, Failure, Success}


class FormRedirectServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, verifier: GoogleVerifier, signer: RSASigner, countries: Countries, tarjonta: Tarjonta) extends HakuperusteetServlet(config, db, oppijanTunnistus, verifier) {

  get("/redirect") {
    failUnlessAuthenticated

    val userData = userDataFromSession
    val hakukohdeOid = params.get("hakukohdeOid").getOrElse(halt(409))
    val applicationObjectForThisHakukohde = db.findApplicationObjectByHakukohdeOid(userDataFromSession, hakukohdeOid).getOrElse(halt(409))
    Try { tarjonta.getApplicationSystem(applicationObjectForThisHakukohde.hakuOid) } match {
      case Success(as) => doRedirect(userData, applicationObjectForThisHakukohde, as)
      case Failure(f) =>
        logger.error("FormRedirectServlet throws", f)
        halt(500)
    }
  }

  def doRedirect(userData: User, applicationObjectForThisHakukohde: ApplicationObject, as: ApplicationSystem): String = {
    val formUrl = as.formUrl
    val payments = db.findPayments(userData)
    val shouldPay = countries.shouldPay(applicationObjectForThisHakukohde.educationCountry)
    val hasPaid = payments.exists(_.status.equals(PaymentStatus.ok))
    write(Map("url" -> formUrl, "params" -> RedirectCreator.generateParamMap(signer, userData, applicationObjectForThisHakukohde, shouldPay, hasPaid)))
  }
}
