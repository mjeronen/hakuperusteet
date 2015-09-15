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

  val json = parse(request.body)
  val token = (json \ "token").extract[Option[String]]
  val idpentityid = (json \ "idpentityid").extract[Option[String]]

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[Session] = {
    (token, idpentityid) match {
      case (Some(tokenFromRequest), Some(idpentityidFromSession)) =>
        db.findSessionByToken(tokenFromRequest) match {
          case s @ Some(session) if session.token == tokenFromRequest => s
          case _ => validateTokenAndCreateSession(tokenFromRequest, idpentityidFromSession)
        }
      case _ => None
    }
  }

  def validateTokenAndCreateSession(tokenFromRequest: String, idpentityidFromSession: String): Option[Session] = {
    oppijanTunnistus.validateToken(tokenFromRequest, idpentityidFromSession) match {
      case Some(session) =>
        logger.info(s"Creating new email session for $session")
        db.upsertSession(session)
        Some(session)
      case _ => None
    }
  }
}
