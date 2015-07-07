import java.nio.file.{Files, Paths}
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Properties
import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet.{StatusServlet, TestServlet}
import org.scalatra.LifeCycle
import org.slf4j.LoggerFactory


class ScalatraBootstrap extends LifeCycle {

  val logger = LoggerFactory.getLogger(this.getClass)

  private def loadProperties(propertiesFilePath: String): Properties = {
    val prop = new Properties()
    val propertiesFile = getClass().getClassLoader().getResource(propertiesFilePath)
    logger.info("Using properties file " + propertiesFile.getPath())
    val propertiesStream = propertiesFile.openStream()
    try {
      prop.load(propertiesStream)
    } finally {
      propertiesStream.close()
    }
    prop
  }

  private def readRSAPrivateKey(filePath: String): RSAPrivateKey = {
    val keyBytes = Files.readAllBytes(Paths.get(getClass.getClassLoader.getResource(filePath).toURI))
    KeyFactory.getInstance("RSA")
      .generatePrivate(new PKCS8EncodedKeySpec(keyBytes))
      .asInstanceOf[RSAPrivateKey]
  }

  override def init(context: ServletContext) {
    val testKey = readRSAPrivateKey("testkey.der")
    context mount(new StatusServlet, "/api/v1/status")
    context mount(new TestServlet(testKey), "/api/v1/test")
  }
}
