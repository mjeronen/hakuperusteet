package fi.vm.sade.hakuperusteet.rsa

import java.io.{File, ByteArrayOutputStream}
import java.security.{Signature, KeyFactory}
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.User
import org.apache.commons.io.FileUtils

class RSASigner(config: Config) extends LazyLogging {
  val key = readPrivateKey()

  def signData(dataString: String) = {
    val signature = Signature.getInstance("SHA256withRSA")
    signature.initSign(key)
    signature.update(dataString.getBytes("UTF-8"))
    Base64.getEncoder.encodeToString(signature.sign())
  }

  private def readPrivateKey(): RSAPrivateKey = {
    val keyPath = config.getString("rsa.sign.key")
    val uri = this.getClass.getClassLoader.getResource(keyPath).toURI
    logger.info(s"Initializing sign key $uri")
    val f = new File(uri)
    val bs = FileUtils.readFileToByteArray(f)
    KeyFactory.getInstance("RSA")
      .generatePrivate(new PKCS8EncodedKeySpec(bs))
      .asInstanceOf[RSAPrivateKey]
  }
}

object RSASigner {
  def init(config: Config) = {
    new RSASigner(config)
  }
}