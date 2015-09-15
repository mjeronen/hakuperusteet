package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import org.scalatra.ScalatraServlet

class HakuperusteetServlet(val configuration: Config, val db: HakuperusteetDatabase, val oppijanTunnistus: OppijanTunnistus) extends ScalatraServlet with AuthenticationSupport with LazyLogging {
  override def realm: String = "hakuperusteet"

  def failUnlessAuthenticated = if (!isAuthenticated) halt(401)

  def userDataFromSession = db.findUser(user.email).getOrElse(halt(500))

  before() {
    contentType = "application/json"
  }
}
