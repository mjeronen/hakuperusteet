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
        createSession(tokenFromRequest)
      case _ => None
    }
  }

  def createSession(tokenFromRequest: String) = {
    Try { oppijanTunnistus.validateToken(tokenFromRequest) } match {
      case Success(Some(email)) =>
        Some(Session(email, tokenFromRequest, tokenName))
      case Success(None) => None
      case Failure(f) =>
        logger.error("Oppijantunnistus.validateToken error", f)
        halt(401)
    }
  }
}
