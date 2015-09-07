package fi.vm.sade.hakuperusteet.rsa

import java.io.ByteArrayOutputStream
import java.security.{Signature, KeyFactory}
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.User

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
    logger.info(s"Initializing sign key $keyPath")
    val in = this.getClass.getClassLoader.getResourceAsStream(keyPath)
    try {
      val out = new ByteArrayOutputStream()
      val buffer: Array[Byte] = new Array(512)
      var i = in.read(buffer, 0, 512)
      while (i > 0) {
        out.write(buffer, 0, i)
        i = in.read(buffer, 0, 512)
      }
      KeyFactory.getInstance("RSA")
        .generatePrivate(new PKCS8EncodedKeySpec(out.toByteArray))
        .asInstanceOf[RSAPrivateKey]
    } finally {
      in.close()
    }
  }
}

object RSASigner {
  def init(config: Config) = {
    new RSASigner(config)
  }
}