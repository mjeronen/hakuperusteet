package fi.vm.sade.hakuperusteet

import java.util.Properties

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

object Hakuperusteet {

  val logger = LoggerFactory.getLogger(this.getClass)

  def loadProperties(propertiesFilePath: String): Config = {
    val prop = new Properties()
    val propertiesFile = getClass().getClassLoader().getResource(propertiesFilePath)
    logger.info("Using properties file " + propertiesFile.getPath())
    val propertiesStream = propertiesFile.openStream()
    prop.load(propertiesStream)
    propertiesStream.close()
    Config(prop.getProperty("port").toInt)
  }

  def main(args: Array[String]) {
    val config: Config = loadProperties("oph-configuration/hakuperusteet.properties")
    val server = new Server(config.port)
    val context = new WebAppContext()
    context setContextPath ("/")
    context.setResourceBase("src/main/webapp")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    server.start
    server.join
  }
}

case class Config(port: Int)
