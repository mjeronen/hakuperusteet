package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.auth.AuthenticationSupport
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import org.scalatra.ScalatraServlet

class HakuperusteetServlet(val configuration: Config, val db: HakuperusteetDatabase) extends ScalatraServlet with AuthenticationSupport {
  override def realm: String = "hakuperusteet"
}
