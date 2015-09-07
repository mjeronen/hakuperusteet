package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import org.scalatra.ScalatraServlet
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}

class PropertiesServlet(config: Config) extends ScalatraServlet {
  implicit val formats = Serialization.formats(NoTypeHints)

  before() {
    contentType = "application/json"
  }

  get("/") {
    val properties = Map(
      "userDataUrl" -> "/hakuperusteet/api/v1/session/userData",
      "vetumaStartUrl" -> "/hakuperusteet/api/v1/vetuma/openvetuma",
      "formRedirectUrl" -> "/hakuperusteet/api/v1/form/redirect",
      "koodistoCountriesUrl" -> config.getString("koodisto.countries.url"),
      "koodistoEeaCountriesUrl" -> config.getString("koodisto.eea.countries.url"),
      "googleAuthenticationClientId" -> config.getString("google.authentication.client.id"),
      "googleAuthenticationHostedDomain" -> config.getString("google.authentication.hosted.domain")
    )
    compact(render(properties))
  }
}


