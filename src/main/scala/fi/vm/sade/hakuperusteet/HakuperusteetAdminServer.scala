package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.Configuration._
import fi.vm.sade.hakuperusteet.util.JettyUtil
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener

/**
 * sbt "run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminServer"
 */
object HakuperusteetAdminServer {

  def main(args: Array[String]) {
    val portHttp = props.getInt("hakuperusteet.port.http")
    val portHttps = Option(props.getInt("hakuperusteet.port.https")).find(_ != -1)

    val server =
      JettyUtil.createServerWithContext(portHttp, portHttps, HakuperusteetAdminContext.createContext)

    server.start
    server.join
  }

}
object HakuperusteetAdminContext {
  def createContext: WebAppContext = {
    val context = new WebAppContext()
    context setContextPath ("/hakuperusteetadmin")
    context.setResourceBase(getClass.getClassLoader.getResource("webapp-admin").toExternalForm)
    context.setInitParameter(ScalatraListener.LifeCycleKey, classOf[ScalatraAdminBootstrap].getCanonicalName)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    context
  }
}