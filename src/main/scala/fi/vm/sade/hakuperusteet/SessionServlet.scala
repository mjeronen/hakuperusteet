package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._
import org.scalatra.ScalatraServlet

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
    db.findUser(email) match {
      case Some(user) =>
        db.findPayment(user) match {
          case Some(payment) => write(SessionData(user, Some(payment)))
          case None => write(SessionData(user, None))
        }
      case None => halt(404, "User not found")
    }
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
