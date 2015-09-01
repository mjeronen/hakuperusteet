package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.User
import fi.vm.sade.hakuperusteet.google.GoogleVerifier._
import fi.vm.sade.hakuperusteet.{HakuperusteetServlet, Configuration}
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryAuthStore.CookieAuthStore
import org.scalatra.auth.{Scentry, ScentryStrategy, ScentrySupport, ScentryConfig}
import org.scalatra.auth.strategy.BasicAuthSupport

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._


trait AuthenticationSupport extends ScentrySupport[User] with BasicAuthSupport[User] { self: HakuperusteetServlet =>
  override abstract def initialize(config: ConfigT) = super.initialize(config)
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  protected def fromSession = { case email: String => db.findUser(email).get  }
  protected def toSession   = { case usr: User => usr.email }

  override protected def registerAuthStrategies = scentry.register("Google", app => new GoogleBasicAuthStrategy(app, configuration, db))
}

class GoogleBasicAuthStrategy(protected override val app: ScalatraBase, config: Config, db: HakuperusteetDatabase) extends ScentryStrategy[User] with LazyLogging {
  import fi.vm.sade.hakuperusteet._

  private def request = app.enrichRequest(app.request)
  val json = parse(request.body)
  val email = (json \ "email").extract[String]
  val token = (json \ "token").extract[String]

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    if (verify(token)) {
      authenticateFromDatabase
    } else {
      logger.error("authenticate: invalid token for email {}", email)
      None
    }
  }

  def authenticateFromDatabase: Option[User] = {
    db.findUser(email) match {
      case Some(user) =>
        logger.info("authenticated user {}", email)
        Some(user)
      case None =>
        None
    }
  }
}