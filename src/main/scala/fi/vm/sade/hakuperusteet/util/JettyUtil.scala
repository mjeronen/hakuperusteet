package fi.vm.sade.hakuperusteet.util

import ch.qos.logback.access.jetty.RequestLogImpl
import com.typesafe.scalalogging.LazyLogging
import org.eclipse.jetty.server._
import org.eclipse.jetty.util.ssl.SslContextFactory
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.Handler

object JettyUtil extends LazyLogging {

  def createServerWithContext(portHttp: Int, portHttps: Option[Int], context: WebAppContext) = {
    val server = new Server()
    server.setHandler(context)
    server.setConnectors(createConnectors(portHttp, portHttps, server))

    val requestLog = new RequestLogImpl()
    requestLog.setFileName(sys.props.getOrElse("logbackaccess.configurationFile","src/main/resources/logbackAccess.xml"))
    server.setRequestLog(requestLog)
    requestLog.start

    initJmx(server)

    server
  }


  private def initJmx(server: Server): Boolean = {
    val jmxPort = System.getProperty("com.sun.management.jmxremote.port", "12345").toInt
    logger.info(s"Starting jmx on $jmxPort")
    val jmx = Jmx.init(jmxPort)
    server.addEventListener(jmx.mBean)
    server.addBean(jmx.mBean)
  }

  private def createConnectors(portHttp: Int, portHttps: Option[Int], server: Server): Array[Connector] = {
    Array(createHttpConnector(portHttp, server)) ++
      portHttps.map(p => Array(createSSLConnector(p,server))).getOrElse(Array())
  }

  private def createHttpConnector(portHttp: Int, server: Server): Connector = {
    val httpConnector = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration))
    httpConnector.setPort(portHttp)
    httpConnector
  }



  private def createSSLConnector(port: Int, server: Server): Connector = {
    val sslContextFactory = new SslContextFactory
    sslContextFactory.setKeyStoreType("jks")
    sslContextFactory.setKeyStorePath(this.getClass.getClassLoader.getResource("keystore").toExternalForm)
    sslContextFactory.setKeyStorePassword("keystore")
    sslContextFactory.setKeyManagerPassword("keystore")

    val httpsConfig = new HttpConfiguration
    httpsConfig.setSecurePort(port)
    httpsConfig.addCustomizer(new SecureRequestCustomizer)

    val https = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(httpsConfig))
    https.setPort(port)
    https
  }
}
