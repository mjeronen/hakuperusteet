package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.email.EmailSender
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.koodisto._
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus

trait ServletTestDependencies extends GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val verifier = new DummyVerifier
  val countries = Countries(List.empty[SimplifiedCode], List.empty)
  val languages = Languages(List.empty[SimplifiedCode])
  val educations = Educations(List.empty[SimplifiedCode])
  val oppijanTunnistus = new DummyOppijanTunnistus(config)
  val emailSender = EmailSender.init(config)
}

class DummyVerifier() extends GoogleVerifier("", "") {
  override def verify(token: String) = true
}

class DummyOppijanTunnistus(c: Config) extends OppijanTunnistus(c) {
  override def createToken(email: String, hakukohdeOid: String) = "dummyLoginToken"
}