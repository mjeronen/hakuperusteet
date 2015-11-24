package fi.vm.sade.hakuperusteet.koodisto

import org.scalatest.{FlatSpec, Matchers}

class KoodistoSpec extends FlatSpec with Matchers {

  val finland = SimplifiedCode("fi", List(SimplifiedLangValue("fi", "fi")))
  val sweden = SimplifiedCode("sv", List(SimplifiedLangValue("sv", "sv")))
  val usa = SimplifiedCode("us", List(SimplifiedLangValue("us", "us")))

  val countries = Countries(List(finland, sweden, usa), List("fi", "sv"))

  it should "require payment when regular base education and from USA" in {
    countries.shouldPay("us", "100") shouldEqual(true)
  }

  it should "not require payment when regular base education and from Finland" in {
    countries.shouldPay("fi", "100") shouldEqual(false)
  }

  it should "not require payment when discretionary base education and from Finland" in {
    countries.shouldPay("fi", "100") shouldEqual(false)
  }

  it should "not require payment when discretionary base education and from USA" in {
    countries.shouldPay("fi", "100") shouldEqual(false)
  }

}
