package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.Session
import fi.vm.sade.hakuperusteet.google.GoogleVerifier
import org.json4s.native.JsonMethods._
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy
import org.scalatra.servlet.RichRequest

class GoogleBasicAuthStrategy(protected override val app: ScalatraBase, config: Config, db: HakuperusteetDatabase, googleVerifier: GoogleVerifier) extends ScentryStrategy[Session] with LazyLogging {
  import fi.vm.sade.hakuperusteet._

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[Session] = {
    val json = parse(RichRequest(request).body)
    val email = (json \ "email").extract[Option[String]]
    val token = (json \ "token").extract[Option[String]] // todo: currently real token
    val idpentityid = (json \ "idpentityid").extract[Option[String]] // todo: currently google
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
    if (googleVerifier.verify(session.token)) {
      db.upsertSession(session)
      Some(session)
    } else {
      logger.warn(s"Session verify failed for user ${session.email} with token ${session.token}")
      None
    }
  }
}