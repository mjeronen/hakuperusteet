package fi.vm.sade.hakuperusteet.admin.auth

import java.net.URLEncoder

import fi.vm.sade.hakuperusteet.HakuperusteetServlet
import fi.vm.sade.hakuperusteet.admin.AdminServlet
import fi.vm.sade.hakuperusteet.auth.{TokenAuthStrategy, GoogleBasicAuthStrategy}
import fi.vm.sade.hakuperusteet.domain.{CasSession, Session}
import fi.vm.sade.security.ldap.LdapConfig
import fi.vm.sade.security.{ProductionSecurityContext, SecurityContext}
import fi.vm.sade.utils.cas.CasClient
import org.scalatra.auth.strategy.BasicAuthSupport
import org.scalatra.auth.{ScentrySupport, ScentryConfig}

import scala.collection.mutable

object CasSessionDB {
  val db = new java.util.concurrent.ConcurrentHashMap[String,CasSession]()

}

trait CasAuthenticationSupport extends ScentrySupport[CasSession] with BasicAuthSupport[CasSession] { self: AdminServlet =>
  override abstract def initialize(config: ConfigT) = super.initialize(config)
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  protected def fromSession = { case username: String => CasSessionDB.db.get(username) }
  protected def toSession = { case usr: CasSession => usr.username }

  override protected def registerAuthStrategies = {
    scentry.register("CAS", app => new CasBasicAuthStrategy(app, cfg))
  }

  val redirectUrl = cfg.getString("hakuperusteet.cas.url") + "/cas/login?service=" + URLEncoder.encode(cfg.getString("adminhost.url.base"), "UTF-8")

  def failUnlessAuthenticated = if (!isAuthenticated) redirect(redirectUrl)
}
