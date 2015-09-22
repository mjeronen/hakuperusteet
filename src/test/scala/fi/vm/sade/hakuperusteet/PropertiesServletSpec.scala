package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.koodisto.Koodisto
import org.scalatest.FunSuite
import org.scalatra.test.scalatest.ScalatraSuite
import org.json4s.native.JsonMethods._

class PropertiesServletSpec extends FunSuite with ScalatraSuite {
  val config = Configuration.props
  val countries = Koodisto.initCountries(config)
  val languages = Koodisto.initLanguages(config)
  val educations = Koodisto.initBaseEducation(config)

  val s = new PropertiesServlet(config, countries, languages, educations)
  addServlet(s, "/*")

  test("get properties json") {
    get("/") {
      status should equal (200)
      val json = parse(body)
      val ghd = (json \ "googleAuthenticationHostedDomain").extract[Option[String]]
      ghd should equal (Some("https://localhost:18080"))
    }
  }
}
