package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, Session, SessionData, User}
import fi.vm.sade.hakuperusteet.email.{EmailSender, EmailTemplate, WelcomeValues}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.util.{AuditLog, ConflictException, ValidationUtil}
import fi.vm.sade.hakuperusteet.validation.{ApplicationObjectValidator, UserValidator}
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._

import scala.util.{Failure, Success, Try}
import scalaz._

class SessionServlet(config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus, verifier: GoogleVerifier, userValidator: UserValidator, applicationObjectValidator: ApplicationObjectValidator, emailSender: EmailSender) extends HakuperusteetServlet(config, db, oppijanTunnistus, verifier) with ValidationUtil {
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
    userValidator.parseEmailToken(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      res => orderEmailToken(res._1, res._2))
  }

  post("/userData") {
    failUnlessAuthenticated
    val params = parse(request.body).extract[Params]
    userValidator.parseUserDataWithoutEmailAndIdpentityid(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      partialUserData => createNewUser(user, partialUserData(user.email, user.idpentityid)))
  }

  post("/educationData") {
    failUnlessAuthenticated
    val params = parse(request.body).extract[Params]
    val userData = userDataFromSession
    applicationObjectValidator.parseApplicationObjectWithoutPersonOid(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      partialEducation => addNewEducation(user, userData, partialEducation(userData.personOid.getOrElse(halt(500))))
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
    val p = WelcomeValues(newUser.fullName)
    emailSender.send(newUser.email, "Studyinfo - Registration successful", EmailTemplate.renderWelcome(p))
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
}
