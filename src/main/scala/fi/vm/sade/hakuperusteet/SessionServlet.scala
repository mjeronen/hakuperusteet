package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.google.GoogleBackendAuthentication
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
    val email = (json \ "email").extract[String]
    val token = (json \ "token").extract[String]

    val googleOk = GoogleBackendAuthentication.authenticate(config, email, token)

    if (googleOk) {
      val sessionData = Map("email"-> email)
      compact(render(sessionData))
    } else {
      halt(status = 403, body = "Backend authentication failed")
    }
  }
}
