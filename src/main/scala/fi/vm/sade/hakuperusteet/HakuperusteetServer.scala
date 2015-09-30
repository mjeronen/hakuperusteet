package fi.vm.sade.hakuperusteet

import ch.qos.logback.access.jetty.RequestLogImpl
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.HakuperusteetServer._
import fi.vm.sade.hakuperusteet.util.{JettyUtil, Jmx}
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler.HandlerCollection
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import Configuration._
import org.slf4j.LoggerFactory

import scala.util.Try

object HakuperusteetServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]) {
    val portHttp = props.getInt("hakuperusteet.port.http")
    val portHttps = Option(props.getInt("hakuperusteet.port.https")).find(_ != -1)

    val server =
      JettyUtil.createServerWithContext(portHttp, portHttps, HakuperusteetContext.createContext)

    server.start
    server.join
    logger.info(s"Hakuperusteet-server started on ports $portHttp and $portHttps")
  }

}
object HakuperusteetContext {
  def createContext: WebAppContext = {
    val context = new WebAppContext()
    context setContextPath ("/hakuperusteet/")
    context.setResourceBase(getClass.getClassLoader.getResource("webapp").toExternalForm)
    context.setInitParameter(ScalatraListener.LifeCycleKey, classOf[ScalatraBootstrap].getCanonicalName)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    context
  }
}
