package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.HttpServletRequest

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{IDPEntityId, Session, User}
import fi.vm.sade.hakuperusteet.henkilo.HenkiloClient
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import org.json4s.native.JsonMethods._
import org.scalatra.Control
import org.scalatra.servlet.RichRequest

import scala.util.{Failure, Success, Try}

class TokenAuthStrategy (config: Config, db: HakuperusteetDatabase, oppijanTunnistus: OppijanTunnistus) extends SimpleAuth with LazyLogging with Control {
  import fi.vm.sade.hakuperusteet._
  val henkiloClient = HenkiloClient.init(config)

  def authenticate(request: HttpServletRequest): Option[Session] = {
    val json = parse(RichRequest(request).body)
    val token = (json \ "token").extract[Option[String]]
    val idpentityid = (json \ "idpentityid").extract[Option[String]]
    (token, idpentityid) match {
      case (Some(tokenFromRequest), Some(idpentityidFromSession)) if idpentityidFromSession == IDPEntityId.oppijaToken.toString => createSession(tokenFromRequest)
      case _ => None
    }
  }

  def createSession(tokenFromRequest: String) = {
    Try { oppijanTunnistus.validateToken(tokenFromRequest) } match {
      case Success(Some((email, Some(metadata)))) => {
        val partialUser: User = User.partialUser(None, Some(metadata.personOid), email, IDPEntityId.oppijaToken)
        upsertIdpEntity(partialUser)
        db.findUser(email).getOrElse(db.upsertPartialUser(partialUser))
        Some(Session(email, tokenFromRequest, IDPEntityId.oppijaToken.toString))
      }
      case Success(Some((email, None))) => Some(Session(email, tokenFromRequest, IDPEntityId.oppijaToken.toString))
      case Success(None) => None
      case Failure(f) =>
        logger.error("Oppijantunnistus.validateToken error", f)
        halt(401)
    }
  }
  
  def upsertIdpEntity(user: User): Unit = {
    Try(henkiloClient.upsertIdpEntity(user).run) match {
      case Success(h) =>
      case Failure(f) => {
        logger.error("henkiloClient.upsertIdpEntity error inserting idp entity for user: " + user, f)
        halt(500)
      }
    }
  }
}

object TokenAuthStrategy {
  import fi.vm.sade.hakuperusteet._

  def hasTokenInRequest(request: HttpServletRequest) = Try((parse(RichRequest(request).body) \ "token").extract[Option[String]].isDefined).getOrElse(false)
}
