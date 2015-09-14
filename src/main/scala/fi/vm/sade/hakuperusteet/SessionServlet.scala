package fi.vm.sade.hakuperusteet

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{SessionData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.koodisto.{Educations, Languages, Countries}
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._

class SessionServlet(config: Config, db: HakuperusteetDatabase, countries: Countries, languages: Languages, educations: Educations) extends HakuperusteetServlet(config, db) {
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

    val params = parse(request.body).extract[Params]
    parseUserData(user.email, user.idpentityid, params) bitraverse (
      errors => {
        contentType = "application/json"
        halt(status = 409, body = compact(render("errors" -> errors.list)))
      },
      usr => {
        logger.info(s"Updating userData: $usr")
        val newUser = Try(HenkiloClient.upsertHenkilo(usr)) match {
          case Success(u) =>
            usr.copy(personOid = Some(u.personOid))
          case Failure(t) =>
            logger.error("Unable to get henkilö", t)
            halt(500, "Unable to get henkilö")
        }
        val userWithId = db.upsertUser(newUser)
        halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(userWithId, Some(countries.shouldPay(newUser.educationCountry)), List.empty))))
      }
      )
  }

  private def parseUserData(email: String, idpentityid: String, params: Params): ValidationResult[User] = {
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

  type ValidationResult[A] = ValidationNel[String, A]
  type Params = Map[String, String]

  def parseExists(key: String)(params: Params) = params.get(key).map(_.successNel)
    .getOrElse(s"Parameter $key does not exist".failureNel)

  def parseNonEmpty(key: String)(params: Params) = parseExists(key)(params)
    .flatMap(a => if (a.nonEmpty) a.successNel else s"Parameter $key is empty".failureNel)

  def parseOptional(key: String)(params: Params) = params.get(key) match { case e => e.successNel }

  private def parseLocalDate(input: String): ValidationResult[LocalDate] =
    Try(LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE).successNel).recover {
      case e => e.getMessage.failureNel
    }.get

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
