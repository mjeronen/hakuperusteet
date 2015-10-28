package fi.vm.sade.hakuperusteet.admin.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.domain.CasSession
import fi.vm.sade.security.ldap.{LdapConfig, LdapClient}
import fi.vm.sade.utils.cas.CasClient
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryStrategy

import scala.util.{Failure, Success, Try}

class CasBasicAuthStrategy(protected override val app: ScalatraBase, cfg: Config) extends ScentryStrategy[CasSession] with LazyLogging {

  logger.warn("Using permit all CAS auth strategy")
  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[CasSession] = {
    Some(CasSession(None, "1.2.3.4", "1.2.3.4", List("APP_HAKUPERUSTEETADMIN_CRUD"), "ticket"))
  }

}
