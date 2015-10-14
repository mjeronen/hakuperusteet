package fi.vm.sade.hakuperusteet.admin

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.auth.CasAuthenticationSupport
import fi.vm.sade.hakuperusteet.db.{HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, UserData, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
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
  val staticFileContent = Source.fromURL(getClass.getResource(resourcePath)).takeWhile(_ != -1).map(_.toByte).toArray
  override def realm: String = "hakuperusteet_admin"
  implicit val formats = fi.vm.sade.hakuperusteet.formatsUI
  val host = cfg.getString("hakuperusteet.cas.url")
  val henkiloClient = HenkiloClient.init(cfg)

  def checkAuthentication = {
    /*
    authenticate
    failUnlessAuthenticated
    */
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
    /*
    if(user.roles.contains("APP_HENKILONHALLINTA_OPHREKISTERI")) {
      val properties = Map("admin" -> true)
      write(properties)
    } else {
      val properties = Map()
      write(properties)
    }
    */
    // TODO What do we want to search here? Do optimized query when search terms are decided!
    write(db.allUsers.filter(u => search.isEmpty || u.email.toLowerCase().contains(search) || (u.firstName + " " + u.lastName).toLowerCase().contains(search)))
  }
  get("/api/v1/admin/:personoid") {
    checkAuthentication
    contentType = "application/json"
    val personOid = params("personoid")
    /*
    if(user.roles.contains("APP_HENKILONHALLINTA_OPHREKISTERI")) {
      val properties = Map("admin" -> true)
      write(properties)
    } else {
      val properties = Map()
      write(properties)
    }
    */
    val user = db.findUserByOid(personOid)
    user match {
      case Some(u) =>
        write(UserData(u, db.findApplicationObjects(u)))

      case _ => {
        halt(status = 404, body = s"User ${personOid} not found")
      }
    }
  }
  post("/api/v1/admin/user") {
    checkAuthentication
    contentType = "application/json"
    val userData = parse(request.body).extract[User]
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
    db.upsertUser(newUser)
    write(UserData(newUser, db.findApplicationObjects(newUser)))
  }
  post("/api/v1/admin/applicationobject") {
    checkAuthentication
    contentType = "application/json"
    val ao = parse(request.body).extract[ApplicationObject]
    db.upsertApplicationObject(ao)
    val user = db.findUserByOid(ao.personOid).get
    write(UserData(user, db.findApplicationObjects(user)))
  }
  error { case e: Throwable => logger.error("uncaught exception", e) }

  def renderConflictWithErrors(errors: NonEmptyList[String]) = halt(status = 409, body = compact(render("errors" -> errors.list)))
}
