package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, Session, SessionData, User}
import fi.vm.sade.hakuperusteet.email.{EmailTemplate, WelcomeValues, EmailSender}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.koodisto.{Educations, Languages, Countries}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.util.{ConflictException, ServerException, AuditLog, ValidationUtil}
import fi.vm.sade.hakuperusteet.validation.ApplicationObjectValidator
import fi.vm.sade.utils.validator.HenkilotunnusValidator
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._

class SessionServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, verifier: GoogleVerifier, countries: Countries, languages: Languages, applicationObjectValidator: ApplicationObjectValidator, emailSender: EmailSender) extends HakuperusteetServlet(config, db, oppijanTunnistus, verifier) with ValidationUtil {
  case class UserDataResponse(field: String, value: SessionData)

  val henkiloClient = HenkiloClient.init(config)
  post("/authenticate") {
    if(!isAuthenticated) {
      authenticate
    }
    failUnlessAuthenticated
    returnUserData
  }

  get("/session") {
    failUnlessAuthenticated
    returnUserData
  }

  post("/logout") {
    logOut()
    "{}"
  }

  post("/emailToken") {
    val params = parse(request.body).extract[Params]
    parseEmailToken(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      res => orderEmailToken(res._1, res._2))
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
    applicationObjectValidator.parseApplicationObjectWithoutPersonOid(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      education => addNewEducation(user, userData, education(userData.personOid.getOrElse(halt(500))))
    )
  }

  private def returnUserData = {
    db.findUser(user.email) match {
      case Some(u) =>
        val educations = db.findApplicationObjects(u).toList
        val payments = db.findPayments(u).toList
        write(SessionData(user, Some(u), educations, payments))
      case None => write(SessionData(user, None, List.empty, List.empty))
    }
  }

  def parseEmailToken(params: Params): ValidationResult[(String, String)] = {
    (parseNonEmpty("email")(params).flatMap(validateEmail) |@| parseExists("hakukohdeOid")(params)) { (email, hakukohdeOid) => (email, hakukohdeOid) }
  }

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))

  def orderEmailToken(email: String, hakukohdeOid: String) =
    Try(oppijanTunnistus.createToken(email, hakukohdeOid)) match {
      case Success(token) =>
        logger.info(s"Sending token to $email with value $token")
        halt(status = 200, body = compact(render(Map("token" -> token))))
      case Failure(f) => logAndHalt("Oppijantunnistus.createToken error", f)
    }

  def createNewUser(session: Session, userData: User) = {
    logger.info(s"Updating userData: $userData")
    val newUser = upsertUserToHenkilo(userData)
    val userWithId = db.upsertUser(newUser)
    sendEmail(newUser)
    AuditLog.auditPostUserdata(userData)
    halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(session, userWithId, List.empty, List.empty))))
  }

  def addNewEducation(session: Session, userData: User, education: ApplicationObject) = {
    logger.info(s"Updating education: $education")
    db.upsertApplicationObject(education)
    val educations = db.findApplicationObjects(userData).toList
    val payments = db.findPayments(userData).toList
    AuditLog.auditPostEducation(userData, education)
    halt(status = 200, body = write(UserDataResponse("sessionData", SessionData(session, Some(userData), educations, payments))))
  }

  private def sendEmail(newUser: User) = {
    val p = WelcomeValues(newUser.email)
    emailSender.send(newUser.email, "Welcome to opintopolku", EmailTemplate.renderWelcome(p))
  }

  def upsertUserToHenkilo(userData: User) = Try(henkiloClient.upsertHenkilo(userData)) match {
      case Success(u) => userData.copy(personOid = Some(u.personOid))
      case Failure(t) if t.isInstanceOf[ConflictException] =>
        val msg = t.getMessage
        logger.error(s"Henkilopalvelu conflict (409) for email ${userData.email} with message $msg")
        renderConflictWithErrors(NonEmptyList[String](msg))
      case Failure(t) => logAndHalt(s"Henkilopalvelu server error for email ${userData.email}", t)
    }

  private def logAndHalt(msg: String, t: Throwable) = {
    logger.error(msg, t)
    halt(status = 500)
  }

  def parseUserData(email: String, idpentityid: String, params: Params): ValidationResult[User] = {
    (parseNonEmpty("firstName")(params)
      |@| parseNonEmpty("lastName")(params)
      |@| parseExists("birthDate")(params).flatMap(parseLocalDate)
      |@| parseOptionalPersonalId(params)
      |@| parseExists("gender")(params).flatMap(validateGender)
      |@| parseExists("nativeLanguage")(params).flatMap(validateNativeLanguage)
      |@| parseExists("nationality")(params).flatMap(validateCountry)
    ) { (firstName, lastName, birthDate, personId, gender, nativeLanguage, nationality) =>
      User(None, None, email, firstName, lastName, java.sql.Date.valueOf(birthDate), personId, idpentityid, gender, nativeLanguage, nationality)
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

  def parseOptionalPersonalId(params: Params): ValidationResult[Option[String]] =
    (params.get("birthDate"), params.get("personId")) match {
      case (Some(b), Some(p)) =>
        parseLocalDate(b) match {
          case scalaz.Success(birthDateParsed) =>
            val pid = birthDateParsed.format(personIdDateFormatter) + p
            HenkilotunnusValidator.validate(pid) match {
              case scalaz.Success(a) => Some(pid).successNel
              case scalaz.Failure(e) => s"invalid pid $pid - [${e.stream.mkString(",")}]".failureNel
            }
          case scalaz.Failure(e) => s"invalid birthDate $b".failureNel
        }
      case _ => None.successNel
    }

  private def validateEmail(email: String): ValidationResult[String] =
    if (!email.isEmpty && email.contains("@") && !email.contains(" ") && !email.contains(",") && !email.contains("\t")) email.successNel
    else s"invalid email $email".failureNel
}
