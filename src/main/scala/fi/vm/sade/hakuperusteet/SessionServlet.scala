package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{SessionData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}

class SessionServlet(config: Config, db: HakuperusteetDatabase) extends HakuperusteetServlet(config, db) {

  post("/authenticate") {
    authenticate
    failUnlessAuthenticated

    db.findUser(user.email) match {
      case Some(user) =>
        db.findPayment(user) match {
          case Some(payment) => write(SessionData(Some(user), Some(payment)))
          case None => write(SessionData(Some(user), None))
        }
      case None => write(SessionData(None, None))
    }
  }

  post("/userData") {
    failUnlessAuthenticated

    val user = parse(request.body).extract[User]
    logger.info(s"Updating userData: $user")

    val newUser = Try(HenkiloClient.upsertHenkilo(user)) match {
      case Success(u) =>
        user.copy(personOid = Some(u.personOid))
      case Failure(t) =>
        logger.error("Unable to get henkilö", t)
        halt(500, "Unable to get henkilö")
    }
    val userWithId = db.upsertUser(newUser)
    write(SessionData(userWithId, None))
  }
}
