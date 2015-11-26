package fi.vm.sade.hakuperusteet.oppijantunnistus

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.domain.User
import fi.vm.sade.hakuperusteet.util.ValidationUtil
import org.apache.http.HttpVersion
import org.apache.http.client.fluent.Request
import org.apache.http.entity.ContentType
import org.json4s.native.JsonMethods._
import org.json4s._
import org.json4s.JsonDSL._

case class OppijanTunnistusVerification(email: Option[String], valid: Boolean, metadata: Option[Map[String,String]])
case class HakuAppMetadata(hakemusOid: String, personOid: String)

case class OppijanTunnistus(c: Config) extends LazyLogging {
  import fi.vm.sade.hakuperusteet._

  def parseHakuAppMetadata(metadata: Map[String, String]): Option[HakuAppMetadata] = {
    val hakemusOid = metadata.get("hakemusOid")
    val personOid = metadata.get("personOid")
    (hakemusOid, personOid) match {
      case (Some(hakemusOid), Some(personOid)) => Some(HakuAppMetadata(hakemusOid, personOid))
      case _ => None
    }
  }

  def createToken(email: String, hakukohdeOid: String) = {
    val siteUrlBase = if (hakukohdeOid.length > 0) s"${c.getString("host.url.base")}ao/$hakukohdeOid/#/token/" else s"${c.getString("host.url.base")}#/token/"
    val data = Map("email" -> email, "url" -> siteUrlBase)

    Request.Post(c.getString("oppijantunnistus.create.url"))
      .useExpectContinue()
      .version(HttpVersion.HTTP_1_1)
      .bodyString(compact(render(data)), ContentType.APPLICATION_JSON)
      .execute().returnContent().asString()
  }

  def validateToken(token: String): Option[(String, Option[HakuAppMetadata])] = {
    logger.info(s"Validating token $token")
    val verifyUrl = c.getString("oppijantunnistus.verify.url") + s"/$token"

    val verifyResult = Request.Get(verifyUrl)
      .useExpectContinue()
      .version(HttpVersion.HTTP_1_1)
      .execute().returnContent().asString()

    val verification = parse(verifyResult).extract[OppijanTunnistusVerification]
    if(verification.valid) {
      verification.email match {
        case Some(email) => Some(email, parseHakuAppMetadata(verification.metadata.getOrElse(Map())))
        case _ => None
      }
    } else {
      None
    }
  }
}

object OppijanTunnistus {
  def init(c: Config) = OppijanTunnistus(c)
}
