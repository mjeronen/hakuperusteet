package fi.vm.sade.hakuperusteet.admin

import java.net.ConnectException

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.auth.{CasSessionDB, CasAuthenticationSupport}
import fi.vm.sade.hakuperusteet.auth.JavaEESessionAuthentication
import fi.vm.sade.hakuperusteet.db.{HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.domain._
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.util.{ValidationUtil, AuditLog}
import fi.vm.sade.hakuperusteet.validation.{PaymentValidator, UserValidator, ApplicationObjectValidator}
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._
import org.scalatra.ScalatraServlet
import com.typesafe.config.Config
import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.{Failure, Success, Try}
import scala.xml.Utility
import scalaz.NonEmptyList
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._
import java.util.Date

class AdminServlet(val resourcePath: String, protected val cfg: Config, userValidator: UserValidator, applicationObjectValidator: ApplicationObjectValidator, db: HakuperusteetDatabase) extends ScalatraServlet with CasAuthenticationSupport with LazyLogging with ValidationUtil {
  val paymentValidator = PaymentValidator()
  val staticFileContent = Source.fromURL(getClass.getResource(resourcePath)).mkString
  override def realm: String = "hakuperusteet_admin"
  implicit val formats = fi.vm.sade.hakuperusteet.formatsUI
  val host = cfg.getString("hakuperusteet.cas.url")
  val henkiloClient = HenkiloClient.init(cfg)

  def checkAuthentication = {
    authenticate
    failUnlessAuthenticated

    if(!user.roles.contains("APP_HAKUPERUSTEETADMIN_CRUD")) {
      logger.error(s"User ${user.username} is unauthorized!")
      halt(401)
    }
  }

  get("/") {
    checkAuthentication
    contentType = "text/html"
    staticFileContent
  }
  post("/") {
    val logoutRequest = params.getOrElse("logoutRequest",halt(500))
    Utility.trim(scala.xml.XML.loadString(logoutRequest)) match {
      case <samlp:LogoutRequest><saml:NameID>{nameID}</saml:NameID><samlp:SessionIndex>{ticket}</samlp:SessionIndex></samlp:LogoutRequest> => {
        CasSessionDB.invalidate(s"${ticket}")
        logger.debug(s"Ticket ${ticket} invalidated!")
        halt(200)
      }
      case _ => {
        logger.error(s"Invalid logout request: ${logoutRequest}")
        halt(500, "Invalid logout request!")
      }
    }
  }
  get("/oppija/*") {
    checkAuthentication
    contentType = "text/html"
    staticFileContent
  }

  get("/api/v1/admin") {
    checkAuthentication
    contentType = "application/json"
    val search = params.getOrElse("search", halt(400)).toLowerCase()
    // TODO What do we want to search here? Do optimized query when search terms are decided!
    write(db.allUsers.filter(u => search.isEmpty || u.email.toLowerCase().contains(search) || (u.firstName + " " + u.lastName).toLowerCase().contains(search)))
  }

  get("/api/v1/admin/:personoid") {
    checkAuthentication
    contentType = "application/json"
    val personOid = params("personoid")
    val user = db.findUserByOid(personOid)
    user match {
      case Some(u) => write(fetchUserData(u))
      case _ => halt(status = 404, body = s"User ${personOid} not found")
    }
  }

  post("/api/v1/admin/user") {
    checkAuthentication
    contentType = "application/json"
    val params = parse(request.body).extract[Params]
    userValidator.parseUserData(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      newUserData => {
        db.findUserByOid(newUserData.personOid.getOrElse(halt(500))) match {
          case Some(oldUserData) =>
            val updatedUserData = oldUserData.copy(firstName = newUserData.firstName, lastName = newUserData.lastName, birthDate = newUserData.birthDate, personId = newUserData.personId, gender = newUserData.gender, nativeLanguage = newUserData.nativeLanguage, nationality = newUserData.nationality)
            Try(henkiloClient.upsertHenkilo(updatedUserData)) match {
              case Success(_) =>
                db.upsertUser(updatedUserData)
                AuditLog.auditAdminPostUserdata(user.oid, updatedUserData)
                syncAndWriteResponse(updatedUserData)
              case Failure(t) if t.isInstanceOf[ConnectException] =>
                logger.error(s"admin-Henkilopalvelu connection error for email ${updatedUserData.email}", t)
                halt(500)
              case Failure(t) =>
                val error = s"admin-Henkilopalvelu upsert failed for email ${updatedUserData.email}"
                logger.error(error, t)
                renderConflictWithErrors(NonEmptyList[String](error))
            }
          case _ => halt(404)
        }
      }
    )
  }

  post("/api/v1/admin/applicationobject") {
    checkAuthentication
    contentType = "application/json"
    val params = parse(request.body).extract[Params]
    applicationObjectValidator.parseApplicationObject(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      education => {
        db.upsertApplicationObject(education)
        val u = db.findUserByOid(education.personOid).get
        AuditLog.auditAdminPostEducation(user.oid, u, education)
        syncAndWriteResponse(u)
      }
    )
  }

  post("/api/v1/admin/payment") {
    checkAuthentication
    contentType = "application/json"
    val params = parse(request.body).extract[Params]
    paymentValidator.parsePaymentWithoutTimestamp(params).bitraverse(
      errors => renderConflictWithErrors(errors),
      partialPayment => {
        val paymentWithoutTimestamp = partialPayment(new Date())
        val u = db.findUserByOid(paymentWithoutTimestamp.personOid).get
        val oldPayment = db.findPaymentByOrderNumber(u, paymentWithoutTimestamp.orderNumber).get
        val payment = partialPayment(oldPayment.timestamp)
        db.upsertPayment(payment)
        AuditLog.auditAdminPayment(user.oid, u, payment)
        syncAndWriteResponse(u)
      }
    )

  }

  error { case e: Throwable => logger.error("uncaught exception", e) }

  private def fetchUserData(u: User): UserData = UserData(u, db.findApplicationObjects(u), db.findPayments(u))

  private def syncAndWriteResponse(u: User) = {
    val data = fetchUserData(u)
    insertSyncRequests(data)
    halt(status = 200, body = write(data))
  }

  private def insertSyncRequests(u: UserData) = u.applicationObject.foreach(db.insertSyncRequest(u.user, _))

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))
}
