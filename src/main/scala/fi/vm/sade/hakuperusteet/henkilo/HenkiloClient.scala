package fi.vm.sade.hakuperusteet.henkilo

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration
import fi.vm.sade.hakuperusteet.domain.{Henkilo, User}
import fi.vm.sade.hakuperusteet.util.CasClientUtils
import fi.vm.sade.utils.cas.{CasAuthenticatingClient, CasClient, CasParams}
import org.http4s.Uri._
import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.json4s.Formats
import org.json4s.native.Serialization.{read, write}

import scalaz.\/._
import scalaz.concurrent.{Future, Task}

object HenkiloClient {
  private val host = Configuration.props.getString("hakuperusteet.cas.url")
  private val username = Configuration.props.getString("hakuperusteet.user")
  private val password = Configuration.props.getString("hakuperusteet.password")

  val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
  val casParams = CasParams("/authentication-service", username, password)
  val henkiloClient = new HenkiloClient(host, new CasAuthenticatingClient(casClient, casParams, org.http4s.client.blaze.defaultClient))

  def upsertHenkilo(user: User) = henkiloClient.haeHenkilo(user).run
}

class HenkiloClient(henkiloServerUrl: String, client: Client) extends LazyLogging with CasClientUtils {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

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
