package fi.vm.sade.hakuperusteet.email

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration
import fi.vm.sade.hakuperusteet.util.CasClientUtils
import fi.vm.sade.utils.cas.{CasAuthenticatingClient, CasClient, CasParams}
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
  private val emailClient = new EmailClient(host, new CasAuthenticatingClient(casClient, casParams, org.http4s.client.blaze.defaultClient))

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

class EmailClient(emailServerUrl: String, client: Client) extends LazyLogging with CasClientUtils {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

  def send(email: EmailData): Task[Response] = client.prepare(req(email)).
    handle {
    case e: ParseException =>
      logger.error(s"emailclient parse error details: ${e.failure.details}", e)
      throw e
    case e =>
      logger.error("emailclient error", e)
      throw e
  }

  private def req(email: EmailData) = Request(
    method = Method.POST,
    uri = resolve(urlToUri(emailServerUrl), Uri(path = "/ryhmasahkoposti-service/email"))
  ).withBody(email)(json4sEncoderOf[EmailData])
}
