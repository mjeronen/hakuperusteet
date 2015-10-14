package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.HttpServletRequest

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.Session
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import org.json4s.native.JsonMethods._
import org.scalatra.Control
import org.scalatra.servlet.RichRequest

import scala.util.{Failure, Success, Try}

class TokenAuthStrategy (config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus) extends SimpleAuth with LazyLogging with Control {
  import fi.vm.sade.hakuperusteet._
  val tokenName = "oppijaToken"

  def authenticate(request: HttpServletRequest): Option[Session] = {
    val json = parse(RichRequest(request).body)
    val token = (json \ "token").extract[Option[String]]
    val idpentityid = (json \ "idpentityid").extract[Option[String]]
    (token, idpentityid) match {
      case (Some(tokenFromRequest), Some(idpentityidFromSession)) if idpentityidFromSession == tokenName =>
        db.findSessionByToken(tokenFromRequest) match {
          case s @ Some(session) => s
          case _ => handleNewSessionOrUpdatedTokenCase(tokenFromRequest)
        }
      case _ => None
    }
  }

  def handleNewSessionOrUpdatedTokenCase(tokenFromRequest: String) = {
    Try { oppijanTunnistus.validateToken(tokenFromRequest) } match {
      case Success(Some(email)) =>
        db.findSession(email) match {
          case Some(session) => updateExistingSessionWithNewToken(tokenFromRequest, email, session)
          case None => createNewSession(tokenFromRequest, email)
        }
      case Success(None) => None
      case Failure(f) =>
        logger.error("Oppijantunnistus.validateToken error", f)
        halt(500)
    }
  }

  def createNewSession(tokenFromRequest: String, email: String) = {
    logger.info(s"Creating new $tokenName session for $email")
    val newSession = Session(None, email, tokenFromRequest, tokenName)
    upsertAndReturn(newSession)
  }

  def updateExistingSessionWithNewToken(tokenFromRequest: String, email: String, session: Session) = {
    logger.info(s"Updating $tokenName session for $email")
    val sessionWithNewToken = session.copy(token = tokenFromRequest)
    upsertAndReturn(sessionWithNewToken)
  }

  def upsertAndReturn(session: Session) = {
    db.upsertSession(session)
    Some(session)
  }
}
