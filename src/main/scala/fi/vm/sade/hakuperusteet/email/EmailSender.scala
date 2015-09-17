package fi.vm.sade.hakuperusteet.email

import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration
import fi.vm.sade.hakuperusteet.henkilo.ActingSystem
import fi.vm.sade.utils.cas.{CasClient, CasAbleClient, CasParams}
import org.http4s.Uri._
import org.http4s._
import org.http4s.client.Client
import org.http4s.headers.`Content-Type`
import org.json4s.native.Serialization._
import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.Serialization.{read, write}
import scalaz.\/._
import scalaz.concurrent.{Future, Task}

object EmailSender extends LazyLogging {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo
  private val host = Configuration.props.getString("hakuperusteet.cas.url")
  private val username = Configuration.props.getString("hakuperusteet.user")
  private val password = Configuration.props.getString("hakuperusteet.password")

  private val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
  private val casParams = CasParams("/ryhmasahkoposti-service", username, password)
  private val emailClient = new EmailClient(host, new CasAbleClient(casClient, casParams, org.http4s.client.blaze.defaultClient))

  def send(to: String, subject: String, body: String): Boolean = {
    val email = EmailMessage("no-reply@opintopolku.fi", subject, body, isHtml = true)
    val recipients = List(EmailRecipient(to))
    val data = EmailData(email, recipients)
    logger.info(s"Sending email ($subject) to $to")
    Status.Ok.equals(emailClient.send(data).run.status)
  }
}

case class EmailRecipient(email: String)
case class EmailMessage(from: String, subject: String, body: String, isHtml: Boolean)
case class EmailData(email: EmailMessage, recipient: List[EmailRecipient])

class EmailClient(emailServerUrl: Uri, client: Client = org.http4s.client.blaze.defaultClient) extends LazyLogging {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

  def this(emailServerUrl: String, client: Client) = this(new Task(
    Future.now(
      Uri.fromString(emailServerUrl).
        leftMap((fail: ParseFailure) => new IllegalArgumentException(fail.sanitized))
    )).run, client)

  def send(email: EmailData): Task[Response] = client.prepare(req(email)).
    handle {
    case e: ParseException =>
      logger.error(s"emailclient parse error details: ${e.failure.details}", e)
      throw e
    case e =>
      logger.error("emailclient error", e)
      throw e
  }

  private def reqHeaders: Headers = Headers(ActingSystem("hakuperusteet.hakuperusteet.backend"))

  private def req(email: EmailData) = Request(
    method = Method.POST,
    uri = resolve(emailServerUrl, Uri(path = "/ryhmasahkoposti-service/email")),
    headers = reqHeaders
  ).withBody(email)(json4sEncoderOf[EmailData])

  def parseJson4s[A] (json:String)(implicit formats: Formats, mf: Manifest[A]) = scala.util.Try(read[A](json)).map(right).recover{
    case t =>
      logger.error("json decoding failed " + json, t)
      left(ParseFailure("json decoding failed", t.getMessage))
  }.get

  def json4sEncoderOf[A <: AnyRef](implicit formats: Formats, mf: Manifest[A]): EntityEncoder[A] = EntityEncoder.stringEncoder(Charset.`UTF-8`).contramap[A](item => write[A](item))
    .withContentType(`Content-Type`(MediaType.`application/json`))

  def json4sOf[A](implicit formats: Formats, mf: Manifest[A]): EntityDecoder[A] = EntityDecoder.decodeBy[A](MediaType.`application/json`){(msg) =>
    DecodeResult(EntityDecoder.decodeString(msg)(Charset.`UTF-8`).map(parseJson4s[A]))
  }
}

