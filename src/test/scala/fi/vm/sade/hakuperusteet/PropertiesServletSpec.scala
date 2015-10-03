package fi.vm.sade.hakuperusteet

import org.scalatest.FunSuite
import org.scalatra.test.scalatest.ScalatraSuite
import org.json4s.native.JsonMethods._

class PropertiesServletSpec extends FunSuite with ScalatraSuite with ServletTestDependencies {
  val s = new PropertiesServlet(config, countries, languages, educations)
  addServlet(s, "/*")

  test("get properties json") {
    get("/") {
      status should equal (200)
      val json = parse(body)
      val ghd = (json \ "googleAuthenticationHostedDomain").extract[Option[String]]
      ghd should equal (Some("https://localhost:18081"))
    }
  }
}
