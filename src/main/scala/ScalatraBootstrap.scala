import org.scalatra.LifeCycle
import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet.StatusServlet

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    context mount (new StatusServlet, "/api/v1/status")
  }
}
