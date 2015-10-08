package fi.vm.sade.hakuperusteet

import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet.admin.AdminServlet
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.email.EmailSender
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.koodisto.Koodisto
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val verifier = GoogleVerifier.init(config)
  val signer = RSASigner.init(config)
  val countries = Koodisto.initCountries(config)
  val languages = Koodisto.initLanguages(config)
  val educations = Koodisto.initBaseEducation(config)
  val tarjonta = Tarjonta.init(config)
  val oppijanTunnistus = OppijanTunnistus.init(config)
  val emailSender = EmailSender.init(config)

  override def init(context: ServletContext) {
    context mount(new IndexServlet, "/ao")
    context mount(new VetumaServlet(config, database, oppijanTunnistus, verifier, emailSender), "/api/v1/vetuma")
    context mount(new TarjontaServlet(tarjonta), "/api/v1/tarjonta")
    context mount(new PropertiesServlet(config, countries, languages, educations), "/api/v1/properties")
    context mount(new SessionServlet(config, database, oppijanTunnistus, verifier, countries, languages, educations, emailSender), "/api/v1/session")
    context mount(new FormRedirectServlet(config, database, oppijanTunnistus, verifier, signer, countries), "/api/v1/form")
  }
}
