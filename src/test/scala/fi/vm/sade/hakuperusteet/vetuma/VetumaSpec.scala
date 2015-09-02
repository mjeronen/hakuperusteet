package fi.vm.sade.hakuperusteet.vetuma

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.Configuration
import org.scalatest.{Matchers, FlatSpec}

class VetumaSpec extends FlatSpec with Matchers {

  behavior of "Vetuma"

  it should "should calculate return mac properly" in {
    val params = List("TESTIASIAKAS11", "20061218154432445", "P2", "fi", "https://localhost/ShowPayment.asp", "https://localhost/ShowCancel.asp",
      "https://localhost/ShowError.asp", "166449462440200", "1234561", "123", "06122588INWX0000", "SUCCESSFUL")

    val sharedSecret = "TESTIASIAKAS11-873C992B8C4C01EC8355500CAA709B37EA43BC2E591ABF29FEE5EAFE4DCBFA35"

    val result = Vetuma.verifyReturnMac(sharedSecret, params, "61397453AAF0A93C2242EFD8813436C58B069B3F38041FC19CD900974B8E9984")
    result shouldEqual true
  }
}
