package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.scalatra.ScalatraServlet

class SessionServlet(config: Config, db: HakuperusteetDatabase) extends ScalatraServlet with AuthenticationSupport {
  override def realm: String = "hakuperusteet"

  before() {
    contentType = "application/json"
  }

  post("/authenticate") {
    authenticate
    failUnlessAuthenticated

    val json = parse(request.body)
    val email = (json \ "email").extract[String]
    val token = (json \ "token").extract[String]

    val sessionData = Map("email"-> email)
    compact(render(sessionData))
  }

  post("/userData") {
    //failUnlessAuthenticated
    val json = parse(request.body)

    println(json)

    val response = Map(
      "field" -> "henkiloOid",
      "value" -> "1.1.1.1")

    //todo: store data to local db
    //todo: create henkilo to henkilopalvelu
    compact(render(response))
  }
}
