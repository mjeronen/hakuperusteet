package fi.vm.sade.hakuperusteet.util

import fi.vm.sade.hakuperusteet.email.{WelcomeValues, EmailTemplate}
import org.scalatest.{FlatSpec, Matchers}

class EmailTemplateSpec extends FlatSpec with Matchers {
  it should "Get translated text" in {
    val welcome: String = EmailTemplate.renderWelcome(WelcomeValues("pow"))
    welcome should include("pow")
    welcome should include("Dear")
    welcome should include("Your registration")
  }
}
