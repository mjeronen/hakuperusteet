package fi.vm.sade.hakuperusteet

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.db.generated.Tables.UserRow
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.scalatra.ScalatraServlet
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import GoogleVerifier._
import java.lang.RuntimeException

import org.slf4j.LoggerFactory

class SessionServlet(config: Config, db: HakuperusteetDatabase) extends ScalatraServlet with AuthenticationSupport with LazyLogging {
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
    println("Email? " + email)
    println("Token? " + token)

    if (verify(token)) {
      logger.info("authenticate: authorized user {}", email)
    } else {
      logger.error("authenticate: unauthorized user {}", email)
      halt(401, "Token verify error")
    }

    val usr = db.findUser(email)
    println(usr)

    val sessionData = Map("email"-> email)
    compact(render(sessionData))
  }

  post("/userData") {
    val user = parse(request.body).extract[User]
    val userWithId = db.insertUser(user)
    //todo: create henkilo to henkilopalvelu
    println(userWithId)

    val response = Map(
      "field" -> "henkiloOid",
      "value" -> user.personOid.getOrElse(""))
    compact(render(response))
  }
}
