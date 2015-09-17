package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.Session
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import org.json4s.native.JsonMethods._
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy

class TokenAuthStrategy (protected override val app: ScalatraBase, config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus) extends ScentryStrategy[Session] with LazyLogging {
  import fi.vm.sade.hakuperusteet._

  private def request = app.enrichRequest(app.request)

  val tokenName = "oppijaToken"

  val json = parse(request.body)
  val token = (json \ "token").extract[Option[String]]
  val idpentityid = (json \ "idpentityid").extract[Option[String]]

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[Session] = {
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
    oppijanTunnistus.validateToken(tokenFromRequest) match {
      case Some(email) =>
        db.findSession(email) match {
          case Some(session) => updateExistingSessionWithNewToken(tokenFromRequest, email, session)
          case None => createNewSession(tokenFromRequest, email)
        }
      case _ => None
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
