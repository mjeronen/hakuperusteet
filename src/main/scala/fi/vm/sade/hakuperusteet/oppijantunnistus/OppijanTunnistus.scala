package fi.vm.sade.hakuperusteet.oppijantunnistus

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.Session
import org.apache.http.HttpVersion
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.json4s.native.JsonMethods._

case class OppijanTunnistus(c: Config) extends LazyLogging {
  import fi.vm.sade.hakuperusteet._

  def createToken(email: String) = {
    val siteUrlBase = c.getString("host.url.base") + "#/token/"
    val body = """{"email":"""" + email + """", "url":"""" + siteUrlBase + """"}"""

    Request.Post(c.getString("oppijantunnistus.create.url"))
      .useExpectContinue()
      .version(HttpVersion.HTTP_1_1)
      .bodyString(body, ContentType.APPLICATION_JSON)
      .execute().returnContent().asString()
  }

  def validateToken(token: String) = {
    logger.info(s"Validating token $token")
    val verifyUrl = c.getString("oppijantunnistus.verify.url") + s"/$token"

    val verifyResult = Request.Get(verifyUrl)
      .useExpectContinue()
      .version(HttpVersion.HTTP_1_1)
      .execute().returnContent().asString()

    val json = parse(verifyResult)
    val valid = (json \ "valid").extract[Option[Boolean]]
    val email = (json \ "email").extract[Option[String]]

    (valid, email) match {
      case (Some(true), Some(emailFromResponse)) => Some(Session(None, emailFromResponse, token, "email"))
      case _ => None
    }
  }
}

object OppijanTunnistus {
  def init(c: Config) = OppijanTunnistus(c)
}
