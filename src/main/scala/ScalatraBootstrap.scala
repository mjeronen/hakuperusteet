import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet._
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

class ScalatraBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)

  val logger = LoggerFactory.getLogger(this.getClass)

  override def init(context: ServletContext) {
    context mount(new StatusServlet, "/api/v1/status")
    context mount(new TestServlet(config), "/api/v1/test")
    context mount(new VetumaServlet(config), "/api/v1/vetuma")
    context mount(new PropertiesServlet(config), "/api/v1/properties")
    context mount(new SessionServlet(config, database), "/api/v1/session")
  }
}
