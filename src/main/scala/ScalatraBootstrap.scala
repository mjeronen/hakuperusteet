import javax.servlet.ServletContext

import com.typesafe.config.ConfigFactory
import fi.vm.sade.hakuperusteet.{VetumaServlet, StatusServlet, TestServlet}
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory


class ScalatraBootstrap extends LifeCycle {

  val logger = LoggerFactory.getLogger(this.getClass)

  override def init(context: ServletContext) {
    val config = ConfigFactory.load()
    context mount(new StatusServlet, "/api/v1/status")
    context mount(new TestServlet(config), "/api/v1/test")
    context mount(new VetumaServlet(config), "/api/v1/vetuma")
  }
}
