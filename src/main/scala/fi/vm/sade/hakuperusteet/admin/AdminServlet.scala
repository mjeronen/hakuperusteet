package fi.vm.sade.hakuperusteet.admin

import java.net.ConnectException

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.auth.CasAuthenticationSupport
import fi.vm.sade.hakuperusteet.db.{HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.domain.{Payment, ApplicationObject, UserData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.util.AuditLog
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization._
import org.scalatra.ScalatraServlet
import com.typesafe.config.Config

import scala.io.Source
import scala.util.{Failure, Success, Try}
import scalaz.NonEmptyList
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization._

class AdminServlet(val resourcePath: String, protected val cfg: Config, db: HakuperusteetDatabase) extends ScalatraServlet with CasAuthenticationSupport with LazyLogging {
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
    val userData = parse(request.body).extract[User]
    val newUser = Try(henkiloClient.upsertHenkilo(userData)) match {
      case Success(u) => userData.copy(personOid = Some(u.personOid))
      case Failure(t) if t.isInstanceOf[ConnectException] =>
        logger.error(s"Henkilopalvelu connection error for email ${userData.email}", t)
        halt(500)
      case Failure(t) =>
        val error = s"Henkilopalvelu upsert failed for email ${userData.email}"
        logger.error(error, t)
        renderConflictWithErrors(NonEmptyList[String](error))
    }
    db.upsertUser(newUser)
    AuditLog.auditAdminPostUserdata(user.oid, newUser)
    syncAndWriteResponse(newUser)
  }

  post("/api/v1/admin/applicationobject") {
    checkAuthentication
    contentType = "application/json"
    val ao = parse(request.body).extract[ApplicationObject]
    db.upsertApplicationObject(ao)
    val u = db.findUserByOid(ao.personOid).get
    AuditLog.auditAdminPostEducation(user.oid, u, ao)
    syncAndWriteResponse(u)
  }

  post("/api/v1/admin/payment") {
    checkAuthentication
    contentType = "application/json"
    val payment = parse(request.body).extract[Payment]
    db.upsertPayment(payment)
    val u = db.findUserByOid(payment.personOid).get
    AuditLog.auditAdminPayment(user.oid, u, payment)
    syncAndWriteResponse(u)
  }

  error { case e: Throwable => logger.error("uncaught exception", e) }

  private def fetchUserData(u: User): UserData = UserData(u, db.findApplicationObjects(u), db.findPayments(u))

  private def syncAndWriteResponse(u: User) = {
    val data = fetchUserData(u)
    insertSyncRequests(data)
    write(data)
  }

  private def insertSyncRequests(u: UserData) =
    u.applicationObject.foreach( (ao) => { db.insertSyncRequest(u.user, ao, "todo") })

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))
}
