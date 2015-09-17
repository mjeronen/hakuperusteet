package fi.vm.sade.hakuperusteet.henkilo

import java.net.URLEncoder

import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.User
import fi.vm.sade.hakuperusteet.domain.Henkilo
import org.http4s.Uri._
import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.{Location, `Content-Type`, `Set-Cookie`}
import org.http4s.util.CaseInsensitiveString
import org.json4s.Formats
import org.json4s.native.Serialization.{read, write}
import scodec.bits.ByteVector
import fi.vm.sade.utils.cas.{CasClient, CasAbleClient, CasParams}

import scala.util.matching.Regex
import scalaz.\/._
import scalaz.concurrent.{Future, Task}
import scalaz.stream.{Channel, Process, async, channel}

import fi.vm.sade.hakuperusteet.{Configuration, formats}

object HenkiloClient {
  private val host = Configuration.props.getString("hakuperusteet.cas.url")
  private val username = Configuration.props.getString("hakuperusteet.user")
  private val password = Configuration.props.getString("hakuperusteet.password")

  val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
  val casParams = CasParams("/authentication-service", username, password)
  val henkiloClient = new HenkiloClient(host, new CasAbleClient(casClient, casParams, org.http4s.client.blaze.defaultClient))

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

  private def reqHeaders: Headers = Headers(ActingSystem("hakuperusteet.hakuperusteet.backend"))

  private def req(user: User) = Request(
    method = Method.POST,
    uri = resolve(henkiloServerUrl, Uri(path = "/authentication-service/resources/s2s/hakuperusteet")),
    headers = reqHeaders //reqHeaders
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

object ActingSystem extends PatternedHeader {
  val headerName = CaseInsensitiveString("Caller-Id")
  override type HeaderT = ActingSystem
  override val pattern: Regex = "([^.]+\\.[^.]+\\.[^.]+)".r

  override def headerForCaptureGroup(group: String): ActingSystem = ActingSystem(group)
}

case class ActingSystem(val id:String) extends Header.Parsed {
  import ActingSystem._

  assert(pattern.pattern.matcher(id).matches())

  override def key: HeaderKey = ActingSystem

  override def renderValue(writer: util.Writer): writer.type = writer << id
}
trait PatternedHeader extends HeaderKey.Singleton {

  val pattern: Regex

  val headerName: CaseInsensitiveString

  def headerForCaptureGroup(group:String):HeaderT

  override def matchHeader(header: Header): Option[HeaderT] = header match {
    case Header(`headerName`, pattern(id)) => Some(headerForCaptureGroup(id))
    case default => None
  }

  override def name: CaseInsensitiveString = headerName
}
