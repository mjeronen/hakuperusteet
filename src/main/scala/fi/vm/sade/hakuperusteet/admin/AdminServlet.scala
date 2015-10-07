package fi.vm.sade.hakuperusteet.admin

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration
import fi.vm.sade.hakuperusteet.admin.auth.CasAuthenticationSupport
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.security.ProductionSecurityContext
import fi.vm.sade.utils.cas.CasClient
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.scalatra.ScalatraServlet
import com.typesafe.config.Config
import fi.vm.sade.security.ldap.LdapConfig
import fi.vm.sade.security.SecurityContext

import scala.io.Source

class AdminServlet(val resourcePath: String, protected val cfg: Config, db: HakuperusteetDatabase) extends ScalatraServlet with CasAuthenticationSupport with LazyLogging {
  val staticFileContent = Source.fromURL(getClass.getResource(resourcePath)).takeWhile(_ != -1).map(_.toByte).toArray
  override def realm: String = "hakuperusteet_admin"
  implicit val formats = Serialization.formats(NoTypeHints)
  val host = cfg.getString("hakuperusteet.cas.url")

  get("/") {
    /*
    authenticate
    failUnlessAuthenticated
    */
    contentType = "text/html"
    staticFileContent
  }
  get("/oppija/*") {
    /*
    authenticate
    failUnlessAuthenticated
    */
    contentType = "text/html"
    staticFileContent
  }

  get("/api/v1/admin") {
    contentType = "application/json"
    /*
    authenticate
    failUnlessAuthenticated

    if(user.roles.contains("APP_HENKILONHALLINTA_OPHREKISTERI")) {
      val properties = Map("admin" -> true)
      write(properties)
    } else {
      val properties = Map()
      write(properties)
    }
    */
    write(db.allUsers)
  }

  error { case e: Throwable => logger.error("uncaught exception", e) }

}
