package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.Configuration._
import fi.vm.sade.hakuperusteet.HakuperusteetServer._
import fi.vm.sade.hakuperusteet.util.JettyUtil
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.resource.ResourceCollection
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

class HakuperusteetServer {

  def portHttp = props.getInt("hakuperusteet.port.http")
  def portHttps = Option(props.getInt("hakuperusteet.port.https")).find(_ != -1)

  def runServer() {
    val dbUrl = props.getString("hakuperusteet.db.url")
    val user = props.getString("hakuperusteet.db.username")
    val password = props.getString("hakuperusteet.db.password")
    val server = JettyUtil.createServerWithContext(portHttp, portHttps, createContext, dbUrl, user, password)
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
    setSecureCookieParams(context)
    context
  }

  def setSecureCookieParams(context: WebAppContext) {
    val sessionCookieConfig = context.getServletContext.getSessionCookieConfig
    sessionCookieConfig.setHttpOnly(true)
    sessionCookieConfig.setSecure(true)
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
