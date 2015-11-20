package fi.vm.sade.hakuperusteet.admin

import java.net.ConnectException

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.auth.{CasSessionDB, CasAuthenticationSupport}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.utils.cas.CasLogout
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
import org.scalatra.json.NativeJsonSupport
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
import org.scalatra.swagger.{SwaggerSupport, Swagger}

class AdminServlet(val resourcePath: String, protected val cfg: Config, oppijanTunnistus: OppijanTunnistus, userValidator: UserValidator, applicationObjectValidator: ApplicationObjectValidator, db: HakuperusteetDatabase)(implicit val swagger: Swagger) extends ScalatraServlet with SwaggerRedirect with CasAuthenticationSupport with LazyLogging with ValidationUtil with SwaggerSupport {
  override protected def applicationDescription: String = "Admin API"
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
    CasLogout.parseTicketFromLogoutRequest(logoutRequest) match {
      case Some(ticket) => CasSessionDB.invalidate(ticket)
      case None => {
        logger.error(s"Invalid logout request: ${logoutRequest}")
        halt(500, "Invalid logout request!")
      }
    }
    halt(200)
  }

  get("/oppija/*") {
    checkAuthentication
    contentType = "text/html"
    staticFileContent
  }

  val getUsers =
    (apiOperation[List[User]]("getUsers")
      summary "Search users"
      notes "Search users by name or email."
      parameter queryParam[Option[String]]("search").description("Search term"))

  get("/api/v1/admin", operation(getUsers)) {
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
        halt(status = 200, body = write(saveUserData(newUserData)))
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
        halt(status = 200, body = write(syncAndWriteResponse(u)))
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
        halt(status = 200, body = write(syncAndWriteResponse(u)))
      }
    )

  }

  error { case e: Throwable => logger.error("uncaught exception", e) }

  private def upsertAndAudit(userData: User) = {
    db.upsertUser(userData)
    AuditLog.auditAdminPostUserdata(user.oid, userData)
    syncAndWriteResponse(userData)
  }

  private def saveUpdatedUserData(updatedUserData: User) = {
    Try(henkiloClient.upsertHenkilo(updatedUserData)) match {
      case Success(_) => upsertAndAudit(updatedUserData)
      case Failure(t) if t.isInstanceOf[ConnectException] =>
        logger.error(s"admin-Henkilopalvelu connection error for email ${updatedUserData.email}", t)
        halt(500, body = "admin-Henkilopalvelu connection error")
      case Failure(t) =>
        val error = s"admin-Henkilopalvelu upsert failed for email ${updatedUserData.email}"
        logger.error(error, t)
        renderConflictWithErrors(NonEmptyList[String](error))
    }
  }

  private def saveUserData(newUserData: User) = {
    db.findUserByOid(newUserData.personOid.getOrElse(halt(500, body = "PersonOid is mandatory"))) match {
      case Some(oldUserData) =>
        val updatedUserData = oldUserData.copy(firstName = newUserData.firstName, lastName = newUserData.lastName, birthDate = newUserData.birthDate, personId = newUserData.personId, gender = newUserData.gender, nativeLanguage = newUserData.nativeLanguage, nationality = newUserData.nationality)
        saveUpdatedUserData(updatedUserData)
      case _ => halt(404)
    }
  }

  private def fetchUserData(u: User): UserData = UserData(u, db.findApplicationObjects(u), db.findPayments(u))

  private def syncAndWriteResponse(u: User) = {
    val data = fetchUserData(u)
    insertSyncRequests(data)
    data
  }

  private def insertSyncRequests(u: UserData) = u.applicationObject.foreach(db.insertSyncRequest(u.user, _))

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))
}
