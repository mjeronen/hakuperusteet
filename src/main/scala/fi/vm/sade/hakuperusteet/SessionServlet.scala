package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.scalatra.ScalatraServlet

class SessionServlet(config: Config) extends ScalatraServlet {
  before() {
    contentType = "application/json"
  }

  post("/") {
    val json = parse(request.body)
    val sessionData = Map("email"-> json \ "email")
    compact(render(sessionData))
  }
}
