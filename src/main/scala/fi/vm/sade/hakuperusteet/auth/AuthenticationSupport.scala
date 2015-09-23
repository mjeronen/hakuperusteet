package fi.vm.sade.hakuperusteet.auth

import fi.vm.sade.hakuperusteet.HakuperusteetServlet
import fi.vm.sade.hakuperusteet.domain.Session
import org.scalatra.auth.strategy.BasicAuthSupport
import org.scalatra.auth.{ScentryConfig, ScentrySupport}


trait AuthenticationSupport extends ScentrySupport[Session] with BasicAuthSupport[Session] { self: HakuperusteetServlet =>
  override abstract def initialize(config: ConfigT) = super.initialize(config)
  protected val scentryConfig = (new ScentryConfig {}).asInstanceOf[ScentryConfiguration]

  protected def fromSession = { case email: String => db.findSession(email).get  }
  protected def toSession   = { case usr: Session => usr.email }

  override protected def registerAuthStrategies = {
    scentry.register("Google", app => new GoogleBasicAuthStrategy(app, configuration, db, googleVerifier))
    scentry.register("Token", app => new TokenAuthStrategy(app, configuration, db, oppijanTunnistus))
  }
}

