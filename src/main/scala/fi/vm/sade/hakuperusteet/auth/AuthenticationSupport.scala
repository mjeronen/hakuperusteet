package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.User
import fi.vm.sade.hakuperusteet.google.GoogleVerifier._
import fi.vm.sade.hakuperusteet.Configuration
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryAuthStore.CookieAuthStore
import org.scalatra.auth.{Scentry, ScentryStrategy, ScentrySupport, ScentryConfig}
import org.scalatra.auth.strategy.BasicAuthSupport

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._


trait AuthenticationSupport extends ScentrySupport[User] with BasicAuthSupport[User] {
  self: ScalatraBase =>

  val configuration = Configuration.props

  protected def fromSession = { case id: String => User.empty(id)  }
  protected def toSession   = { case usr: User => usr.email }

  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  override protected def configureScentry = {
    val authCookieOptions = cookieOptions.copy(domain = configuration.getString("cookie.domain"), secure = true, httpOnly = true)
    scentry.store = new CookieAuthStore(self)(authCookieOptions) {
      override def invalidate()(implicit request: HttpServletRequest, response: HttpServletResponse) {
        cookies.update(Scentry.scentryAuthKey, "")(authCookieOptions.copy(maxAge = 0))
      }
    }

    scentry.unauthenticated {
      scentry.strategies("Google").unauthenticated()
    }
  }
  override protected def registerAuthStrategies = {
    scentry.register("Google", app => new GoogleBasicAuthStrategy(app, configuration))
  }

  def failUnlessAuthenticated = if (!isAuthenticated) halt(401)
}

class GoogleBasicAuthStrategy(protected override val app: ScalatraBase, config: Config) extends ScentryStrategy[User] with LazyLogging {
  import fi.vm.sade.hakuperusteet._

  private def request = app.enrichRequest(app.request)
  val json = parse(request.body)
  val email = (json \ "email").extract[String]
  val token = (json \ "token").extract[String]

  def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[User] = {
    if (verify(token)) {
      logger.info("authenticate: authorized user {}", email)
      Some(User.empty(email))
    } else {
      logger.error("authenticate: unauthorized user {}", email)
      None
    }
  }
}