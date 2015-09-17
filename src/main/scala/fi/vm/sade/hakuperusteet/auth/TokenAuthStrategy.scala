package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
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
          case s @ Some(session) if session.token == tokenFromRequest => s
          case Some(session) => validateNewTokenAndUpdateSession(session.copy(token = tokenFromRequest))
          case _ => validateTokenAndCreateSession(tokenFromRequest)
        }
      case _ => None
    }
  }

  def validateNewTokenAndUpdateSession(sessionWithNewToken: Session) = {
    oppijanTunnistus.validateToken(sessionWithNewToken.token) match {
      case Some(email) =>
        logger.info(s"Updating $tokenName session for $email")
        db.upsertSession(sessionWithNewToken)
        Some(sessionWithNewToken)
      case _ => None
    }
  }

  def validateTokenAndCreateSession(tokenFromRequest: String) = {
    oppijanTunnistus.validateToken(tokenFromRequest) match {
      case Some(email) =>
        val newSession = Session(None, email, tokenFromRequest, tokenName)
        logger.info(s"Creating new $tokenName session for $email")
        db.upsertSession(newSession)
        Some(newSession)
      case _ => None
    }
  }
}
