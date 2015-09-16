package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{SessionData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.koodisto.{Educations, Languages, Countries}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.util.ValidationUtil
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._

class SessionServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, countries: Countries, languages: Languages, educations: Educations) extends HakuperusteetServlet(config, db, oppijanTunnistus) with ValidationUtil {
  case class UserDataResponse(field: String, value: SessionData)

  post("/authenticate") {
    authenticate
    failUnlessAuthenticated

    db.findUser(user.email) match {
      case Some(u) => write(SessionData(user.email, Some(u), Some(countries.shouldPay(u.educationCountry)), db.findPayments(u).toList))
      case None => write(SessionData(user.email, None, None, List.empty))
    }
  }

  get("/sessionData") {
    failUnlessAuthenticated
    write(user)
  }

  post("/logout") {
    logOut()
    "{}"
  }

  post("/userData") {
    failUnlessAuthenticated
    val params = parse(request.body).extract[Params]
    parseUserData(user.email, user.idpentityid, params).bitraverse(
      errors => renderConflictWithErrors(errors),
      userData => createNewUser(userData))
  }

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))

  def createNewUser(userData: User) = {
    logger.info(s"Updating userData: $userData")
    val newUser = upsertUserToHenkilo(userData)
    val userWithId = db.upsertUser(newUser)
    halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(userData.email, userWithId, Some(countries.shouldPay(newUser.educationCountry)), List.empty))))
  }

  def upsertUserToHenkilo(userData: User): User = {
    val newUser = Try(HenkiloClient.upsertHenkilo(userData)) match {
      case Success(u) => userData.copy(personOid = Some(u.personOid))
      case Failure(t) =>
        logger.error("Unable to get henkilö", t)
        halt(500, "Unable to get henkilö")
    }
    newUser
  }

  def parseUserData(email: String, idpentityid: String, params: Params): ValidationResult[User] = {
    (parseNonEmpty("firstName")(params)
      |@| parseNonEmpty("lastName")(params)
      |@| parseExists("birthDate")(params).flatMap(parseLocalDate)
      |@| parseOptional("personId")(params)
      |@| parseExists("gender")(params).flatMap(validateGender)
      |@| parseExists("nativeLanguage")(params).flatMap(validateNativeLanguage)
      |@| parseExists("nationality")(params).flatMap(validateCountry)
      |@| parseExists("educationLevel")(params).flatMap(validateEducationLevel)
      |@| parseExists("educationCountry")(params).flatMap(validateCountry)
    ) { (firstName, lastName, birthDate, personId, gender, nativeLanguage, nationality, educationLevel, educationCountry) =>
      User(None, None, email, firstName, lastName, java.sql.Date.valueOf(birthDate), personId, idpentityid, gender, nativeLanguage, nationality, educationLevel, educationCountry)
    }
  }

  private def validateGender(gender: String): ValidationResult[String] =
    if (gender == "1" || gender == "2") gender.successNel
    else  s"gender value $gender is invalid".failureNel

  private def validateNativeLanguage(nativeLanguage: String): ValidationResult[String] =
    if (languages.languages.map(_.id).contains(nativeLanguage)) nativeLanguage.successNel
    else s"unknown nativeLanguage $nativeLanguage".failureNel

  private def validateCountry(nationality: String): ValidationResult[String] =
    if (countries.countries.map(_.id).contains(nationality)) nationality.successNel
    else s"unknown country $nationality".failureNel

  private def validateEducationLevel(educationLevel: String): ValidationResult[String] =
    if (educations.educations.map(_.id).contains(educationLevel)) educationLevel.successNel
    else s"unknown educationLevel $educationLevel".failureNel
}
