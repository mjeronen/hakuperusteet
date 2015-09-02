package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{Session, User}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier._
import fi.vm.sade.hakuperusteet.{HakuperusteetServlet, Configuration}
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryAuthStore.CookieAuthStore
import org.scalatra.auth.{Scentry, ScentryStrategy, ScentrySupport, ScentryConfig}
import org.scalatra.auth.strategy.BasicAuthSupport

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._


trait AuthenticationSupport extends ScentrySupport[Session] with BasicAuthSupport[Session] { self: HakuperusteetServlet =>
  override abstract def initialize(config: ConfigT) = super.initialize(config)
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  protected def fromSession = { case email: String => db.findSession(email).get  }
  protected def toSession   = { case usr: Session => usr.email }

  override protected def registerAuthStrategies = scentry.register("Google", app => new GoogleBasicAuthStrategy(app, configuration, db))
}

class GoogleBasicAuthStrategy(protected override val app: ScalatraBase, config: Config, db: HakuperusteetDatabase) extends ScentryStrategy[Session] with LazyLogging {
  import fi.vm.sade.hakuperusteet._

  private def request = app.enrichRequest(app.request)

  val json = parse(request.body)
  val email = (json \ "email").extract[Option[String]]
  val token = (json \ "token").extract[Option[String]] // todo: currently real token
  val idpentityid = (json \ "idpentityid").extract[Option[String]] // todo: currently google

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[Session] = {
    (email, token, idpentityid) match {
      case (Some(emailFromRequest), Some(tokenFromRequest), Some(idpentityidFromSession)) =>
        db.findSession(emailFromRequest) match {
          case s @ Some(session) if session.token == tokenFromRequest => s
          case Some(session) =>
            logger.info(s"Updating session token for $emailFromRequest")
            verifyAndCreateSession(session.copy(token = tokenFromRequest))
          case _ =>
            logger.info(s"Creating new session for $emailFromRequest")
            val newSession = Session(None, emailFromRequest, tokenFromRequest, idpentityidFromSession)
            verifyAndCreateSession(newSession)
        }
      case _ => None
    }
  }

  private def verifyAndCreateSession(session: Session): Option[Session] = {
    if (verify(session.token)) {
      db.upsertSession(session)
    } else {
      logger.warn(s"Session verify failed for user ${session.email} with token ${session.token}")
      None
    }
  }
}