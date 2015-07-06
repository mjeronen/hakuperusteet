import java.util.Properties
import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet.{StatusServlet, TestServlet}
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._


class ScalatraBootstrap extends LifeCycle {

  val logger = LoggerFactory.getLogger(this.getClass)

  private def loadProperties(propertiesFilePath: String): Properties = {
    val prop = new Properties()
    val propertiesFile = getClass().getClassLoader().getResource(propertiesFilePath)
    logger.info("Using properties file " + propertiesFile.getPath())
    val propertiesStream = propertiesFile.openStream()
    prop.load(propertiesStream)
    propertiesStream.close()
    prop
  }

  override def init(context: ServletContext) {
    val secrets = loadProperties("secrets.properties").asScala.toMap
    context mount (new StatusServlet, "/api/v1/status")
    context mount (new TestServlet(secrets), "/api/v1/test")
  }
}
