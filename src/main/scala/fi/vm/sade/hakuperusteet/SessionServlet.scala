package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.google.GoogleBackendAuthentication
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.scalatra.ScalatraServlet

class SessionServlet(config: Config) extends ScalatraServlet with AuthenticationSupport {
  override def realm: String = "hakuperusteet"

  before() {
    contentType = "application/json"
  }

  post("/") {
    authenticate
    failUnlessAuthenticated

    val json = parse(request.body)
    val email = (json \ "email").extract[String]
    val token = (json \ "token").extract[String]

    val sessionData = Map("email"-> email)
    compact(render(sessionData))
  }
}
