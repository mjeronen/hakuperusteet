package fi.vm.sade.hakuperusteet

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatra.ScalatraServlet
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

class PropertiesServlet(configuration: Config) extends ScalatraServlet {
  before() {
    contentType = formats("json")
  }

  get("/") {
    val properties = Map("koodistoCountriesUrl" -> ConfigFactory.load().getString("koodisto.countries.url"))
    compact(render(properties))
  }
}


