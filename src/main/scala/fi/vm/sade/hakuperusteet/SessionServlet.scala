package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{SessionData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.koodisto.Countries
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}

class SessionServlet(config: Config, db: HakuperusteetDatabase, countries: Countries) extends HakuperusteetServlet(config, db) {
  case class UserDataResponse(field: String, value: SessionData)

  post("/authenticate") {
    authenticate
    failUnlessAuthenticated

    db.findUser(user.email) match {
      case Some(u) => write(SessionData(Some(u), Some(countries.shouldPay(u.educationCountry)), db.findPayments(u).toList))
      case None => write(SessionData(None, None, List.empty))
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
    write(UserDataResponse("sessionData", SessionData(userWithId, Some(countries.shouldPay(newUser.educationCountry)), List.empty)))
  }
}
