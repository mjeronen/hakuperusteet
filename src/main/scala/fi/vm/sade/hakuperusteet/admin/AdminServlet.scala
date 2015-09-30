package fi.vm.sade.hakuperusteet.admin

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.auth.CasAuthenticationSupport
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.security.ProductionSecurityContext
import fi.vm.sade.utils.cas.CasClient
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.scalatra.ScalatraServlet
import com.typesafe.config.Config
import fi.vm.sade.security.ldap.LdapConfig
import fi.vm.sade.security.SecurityContext

class AdminServlet(protected val cfg: Config) extends ScalatraServlet with CasAuthenticationSupport with LazyLogging {
  override def realm: String = "hakuperusteet_admin"

  implicit val formats = Serialization.formats(NoTypeHints)

  val host = cfg.getString("hakuperusteet.cas.url")

  before() {
    contentType = "application/json"
  }


  get("/") {
    authenticate
    failUnlessAuthenticated

    if(user.roles.contains("APP_HENKILONHALLINTA_OPHREKISTERI")) {
      val properties = Map("admin" -> true)
      write(properties)
    } else {
      val properties = Map()
      write(properties)
    }
  }

  error { case e: Throwable => logger.error("uncaught exception", e) }

}
