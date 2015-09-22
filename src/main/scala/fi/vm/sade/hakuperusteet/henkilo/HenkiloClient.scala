package fi.vm.sade.hakuperusteet.henkilo

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.domain.{Henkilo, User}
import fi.vm.sade.hakuperusteet.util.CasClientUtils
import fi.vm.sade.utils.cas.{CasAuthenticatingClient, CasClient, CasParams}
import org.http4s.Uri._
import org.http4s._
import org.http4s.client.Client

import scalaz.concurrent.Task

object HenkiloClient {
  def init(c: Config) = {
    val host = c.getString("hakuperusteet.cas.url")
    val username = c.getString("hakuperusteet.user")
    val password = c.getString("hakuperusteet.password")
    val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
    val casParams = CasParams("/authentication-service", username, password)
    new HenkiloClient(host, new CasAuthenticatingClient(casClient, casParams, org.http4s.client.blaze.defaultClient))
  }
}

class HenkiloClient(henkiloServerUrl: String, client: Client) extends LazyLogging with CasClientUtils {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

  def upsertHenkilo(user: User) = haeHenkilo(user).run

  def haeHenkilo(user: User): Task[Henkilo] = client.prepAs[Henkilo](req(user))(json4sOf[Henkilo]).
    handle {
    case e: ParseException =>
      logger.error(s"parse error details: ${e.failure.details}")
      throw e
    case e =>
      logger.error(s"error: $e")
      throw e
  }

  private def req(user: User) = Request(
    method = Method.POST,
    uri = resolve(urlToUri(henkiloServerUrl), Uri(path = "/authentication-service/resources/s2s/hakuperusteet"))
  ).withBody(user)(json4sEncoderOf[User])
}
