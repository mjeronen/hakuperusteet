package fi.vm.sade.hakuperusteet.koodisto

import fi.vm.sade.hakuperusteet.util.Translate
import org.scalatest.{FlatSpec, Matchers}

class TranslationSpec extends FlatSpec with Matchers {

  it should "require payment when regular base education and from USA" in {
    Translate("pow.zap", "fi") shouldEqual "powpow!"
  }
}
