package fi.vm.sade.hakuperusteet.oppijantunnistus

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.http.HttpVersion
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.JsonDSL._

case class OppijanTunnistus(c: Config) extends LazyLogging {
  import fi.vm.sade.hakuperusteet._

  def createToken(email: String, hakukohdeOid: String) = {
    val siteUrlBase = if (hakukohdeOid.length > 0) s"${c.getString("host.url.base")}ao/$hakukohdeOid/#/token/" else s"${c.getString("host.url.base")}#/token/"
    val data = Map("email" -> email, "url" -> siteUrlBase)

    Request.Post(c.getString("oppijantunnistus.create.url"))
      .useExpectContinue()
      .version(HttpVersion.HTTP_1_1)
      .bodyString(compact(render(data)), ContentType.APPLICATION_JSON)
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
      case (Some(true), Some(emailFromResponse)) => Some(emailFromResponse)
      case _ => None
    }
  }
}

object OppijanTunnistus {
  def init(c: Config) = OppijanTunnistus(c)
}
