package fi.vm.sade.hakuperusteet

import java.time.LocalDate
import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, Session, SessionData, User}
import fi.vm.sade.hakuperusteet.email.{EmailTemplate, WelcomeValues, EmailSender}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
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

class SessionServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, verifier: GoogleVerifier, countries: Countries, languages: Languages, educations: Educations, emailSender: EmailSender) extends HakuperusteetServlet(config, db, oppijanTunnistus, verifier) with ValidationUtil {
  case class UserDataResponse(field: String, value: SessionData)

  val henkiloClient = HenkiloClient.init(config)
  post("/authenticate") {
    authenticate
    failUnlessAuthenticated

    db.findUser(user.email) match {
      case Some(u) =>
        val educations = db.findApplicationObjects(u).toList
        val payments = db.findPayments(u).toList
        write(SessionData(user, Some(u), educations, payments))
      case None => write(SessionData(user, None, List.empty, List.empty))
    }
  }

  get("/session") {
    failUnlessAuthenticated
    write(user)
  }

  post("/logout") {
    logOut()
    "{}"
  }

  post("/emailToken") {
    val params = parse(request.body).extract[Params]
    parseNonEmpty("email")(params).flatMap(validateEmail).bitraverse(
      errors => renderConflictWithErrors(errors),
      email => orderEmailToken(email))
  }

  post("/userData") {
    failUnlessAuthenticated
    val params = parse(request.body).extract[Params]
    parseUserData(user.email, user.idpentityid, params).bitraverse(
      errors => renderConflictWithErrors(errors),
      userData => createNewUser(user, userData))
  }

  post("/educationData") {
    failUnlessAuthenticated
    val params = parse(request.body).extract[Params]
    val userData = userDataFromSession
    parseEducationData(userData.personOid.getOrElse(halt(500)), params).bitraverse(
      errors => renderConflictWithErrors(errors),
      education => addNewEducation(user, userData, education))
  }

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))

  def orderEmailToken(email: String) =
    Try(oppijanTunnistus.createToken(email)) match {
      case Success(token) =>
        logger.info(s"Sending token to $email with value $token")
        halt(status = 200, body = compact(render(Map("token" -> token))))
      case Failure(f) =>
        logger.error("Oppijantunnistus.createToken error", f)
        halt(status = 500)
    }

  def createNewUser(session: Session, userData: User) = {
    logger.info(s"Updating userData: $userData")
    val newUser = upsertUserToHenkilo(userData)
    val userWithId = db.upsertUser(newUser)
    sendEmail(newUser)
    halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(session, userWithId, List.empty, List.empty))))
  }

  def addNewEducation(session: Session, userData: User, education: ApplicationObject) = {
    logger.info(s"Updating education: $education")
    db.upsertApplicationObject(education)
    val educations = db.findApplicationObjects(userData).toList
    val payments = db.findPayments(userData).toList
    halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(session, Some(userData), educations, payments))))
  }

  private def sendEmail(newUser: User): Boolean = {
    val p = WelcomeValues(newUser.email)
    emailSender.send(newUser.email, "Welcome to opintopolku", EmailTemplate.renderWelcome(p))
  }

  def upsertUserToHenkilo(userData: User): User = {
    val newUser = Try(henkiloClient.upsertHenkilo(userData)) match {
      case Success(u) => userData.copy(personOid = Some(u.personOid))
      case Failure(t) if t.isInstanceOf[java.net.ConnectException] =>
        logger.error(s"Henkilopalvelu connection error for email ${userData.email}", t)
        halt(500)
      case Failure(t) =>
        val error = s"Henkilopalvelu upsert failed for email ${userData.email}"
        logger.error(error, t)
        renderConflictWithErrors(NonEmptyList[String](error))
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
    ) { (firstName, lastName, birthDate, personId, gender, nativeLanguage, nationality) =>
      User(None, None, email, firstName, lastName, java.sql.Date.valueOf(birthDate), createPersonalId(birthDate, personId),
        idpentityid, gender, nativeLanguage, nationality)
    }
  }

  def parseEducationData(personOid: String, params: Params): ValidationResult[ApplicationObject] = {
    (parseNonEmpty("hakukohdeOid")(params)
      |@| parseNonEmpty("hakuOid")(params)
      |@| parseExists("educationLevel")(params).flatMap(validateEducationLevel)
      |@| parseExists("educationCountry")(params).flatMap(validateCountry)
      ) { (hakukohdeOid, hakuOid, educationLevel, educationCountry) =>
      ApplicationObject(None, personOid, hakukohdeOid, hakuOid, educationLevel, educationCountry)
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

  private def createPersonalId(birthDate: LocalDate, personId: Option[String]) =
    personId.map { (pid) => birthDate.format(personIdDateFormatter) + pid }

  private def validateEmail(email: String): ValidationResult[String] =
    if (!email.isEmpty && email.contains("@") && !email.contains(" ") && !email.contains(",") && !email.contains("\t")) email.successNel
    else s"invalid email $email".failureNel
}
