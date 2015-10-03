package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.Configuration._
import fi.vm.sade.hakuperusteet.HakuperusteetAdminServer._
import fi.vm.sade.hakuperusteet.util.JettyUtil
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

class HakuperusteetAdminServer extends HakuperusteetServer {
  override def createContext = {
    val context = new WebAppContext()
    context setContextPath ("/hakuperusteetadmin")
    context.setResourceBase(getClass.getClassLoader.getResource("webapp-admin").toExternalForm)
    context.setInitParameter(ScalatraListener.LifeCycleKey, classOf[ScalatraAdminBootstrap].getCanonicalName)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    context
  }
}

/**
 * ./sbt "run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminServer"
 */
object HakuperusteetAdminServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) {
    val s = new HakuperusteetAdminServer
    s.runServer()
    logger.info("Started HakuperusteetAdminServer")
  }
}
