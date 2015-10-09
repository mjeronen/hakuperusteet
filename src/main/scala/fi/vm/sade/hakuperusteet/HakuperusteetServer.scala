package fi.vm.sade.hakuperusteet

import javax.servlet.SessionCookieConfig

import fi.vm.sade.hakuperusteet.Configuration._
import fi.vm.sade.hakuperusteet.HakuperusteetServer._
import fi.vm.sade.hakuperusteet.util.JettyUtil
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.resource.ResourceCollection
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

class HakuperusteetServer {
  def runServer() {
    val portHttp = props.getInt("hakuperusteet.port.http")
    val portHttps = Option(props.getInt("hakuperusteet.port.https")).find(_ != -1)
    val server = JettyUtil.createServerWithContext(portHttp, portHttps, createContext)
    server.start
    server.join
    logger.info(s"Using ports $portHttp and $portHttps")
  }

  def createContext = {
    val context = new WebAppContext()
    val resources = new ResourceCollection(Array(
      getClass.getClassLoader.getResource("webapp-common").toExternalForm,
      getClass.getClassLoader.getResource("webapp").toExternalForm
    ))
    context setContextPath ("/hakuperusteet/")
    context.setBaseResource(resources)
    context.setInitParameter(ScalatraListener.LifeCycleKey, classOf[ScalatraBootstrap].getCanonicalName)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    val sessionCookieConfig = context.getServletContext.getSessionCookieConfig
    sessionCookieConfig.setHttpOnly(true)
    sessionCookieConfig.setSecure(true)
    context
  }
}

object HakuperusteetServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val s = new HakuperusteetServer
    s.runServer()
    logger.info("Started HakuperusteetServer")
  }
}
