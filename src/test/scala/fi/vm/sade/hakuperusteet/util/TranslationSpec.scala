package fi.vm.sade.hakuperusteet.koodisto

import fi.vm.sade.hakuperusteet.util.Translate
import org.scalatest.{FlatSpec, Matchers}

class TranslationSpec extends FlatSpec with Matchers {
  it should "Get translated text" in {
    Translate("test", "testi", "fi") shouldEqual "powpow!"
    Translate("test.testi", "fi") shouldEqual "powpow!"
  }

  it should "Get translation map" in {
    Translate.get("test", "testi") shouldEqual Map("fi" -> "powpow!")
  }
}
