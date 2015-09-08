import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet._
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.koodisto.Countries
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val signer = RSASigner.init(config)
  val countries = Countries.init(config)

  override def init(context: ServletContext) {
    context mount(new StatusServlet, "/api/v1/status")
    context mount(new TestServlet(config), "/api/v1/test")
    context mount(new VetumaServlet(config, database), "/api/v1/vetuma")
    context mount(new PropertiesServlet(config), "/api/v1/properties")
    context mount(new SessionServlet(config, database, countries), "/api/v1/session")
    context mount(new FormRedirectServlet(config, database, signer, countries), "/api/v1/form")
  }
}
