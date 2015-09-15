package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{Session, User}
import fi.vm.sade.hakuperusteet.google.GoogleVerifier._
import fi.vm.sade.hakuperusteet.{HakuperusteetServlet, Configuration}
import org.scalatra.ScalatraBase
import org.scalatra.auth.ScentryAuthStore.CookieAuthStore
import org.scalatra.auth.{Scentry, ScentryStrategy, ScentrySupport, ScentryConfig}
import org.scalatra.auth.strategy.BasicAuthSupport

import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._


trait AuthenticationSupport extends ScentrySupport[Session] with BasicAuthSupport[Session] { self: HakuperusteetServlet =>
  override abstract def initialize(config: ConfigT) = super.initialize(config)
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  protected def fromSession = { case email: String => db.findSession(email).get  }
  protected def toSession   = { case usr: Session => usr.email }

  override protected def registerAuthStrategies = {
    scentry.register("Google", app => new GoogleBasicAuthStrategy(app, configuration, db))
    scentry.register("Token", app => new TokenAuthStrategy(app, configuration, db, oppijanTunnistus))
  }
}

