package fi.vm.sade.hakuperusteet.henkilo

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration
import fi.vm.sade.hakuperusteet.domain.{Henkilo, User}
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

class HenkiloClient(henkiloServerUrl: Uri, client: Client = org.http4s.client.blaze.defaultClient) extends LazyLogging {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

  def this(henkiloServerUrl: String, client: Client) = this(new Task(
    Future.now(
      Uri.fromString(henkiloServerUrl).
        leftMap((fail: ParseFailure) => new IllegalArgumentException(fail.sanitized))
    )).run, client)

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
    uri = resolve(henkiloServerUrl, Uri(path = "/authentication-service/resources/s2s/hakuperusteet"))
  ).withBody(user)(json4sEncoderOf[User])

  def parseJson4s[A] (json:String)(implicit formats: Formats, mf: Manifest[A]) = scala.util.Try(read[A](json)).map(right).recover{
    case t =>
      logger.error("json decoding failed {}!",json, t)
      left(ParseFailure("json decoding failed", t.getMessage))
  }.get

  def json4sEncoderOf[A <: AnyRef](implicit formats: Formats, mf: Manifest[A]): EntityEncoder[A] = EntityEncoder.stringEncoder(Charset.`UTF-8`).contramap[A](item => write[A](item))
  .withContentType(`Content-Type`(MediaType.`application/json`))

  def json4sOf[A](implicit formats: Formats, mf: Manifest[A]): EntityDecoder[A] = EntityDecoder.decodeBy[A](MediaType.`application/json`){(msg) =>
    DecodeResult(EntityDecoder.decodeString(msg)(Charset.`UTF-8`).map(parseJson4s[A]))
  }
}
