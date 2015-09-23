package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.email.EmailSender
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.koodisto.Koodisto
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus

trait ServletTestDependencies extends GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val verifier = new DummyVerifier
  val countries = Koodisto.initCountries(config)
  val languages = Koodisto.initLanguages(config)
  val educations = Koodisto.initBaseEducation(config)
  val oppijanTunnistus = OppijanTunnistus.init(config)
  val emailSender = EmailSender.init(config)
}

class DummyVerifier() extends GoogleVerifier("", "") {
  override def verify(token: String) = true
}