package fi.vm.sade.hakuperusteet.email

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.util.CasClientUtils
import fi.vm.sade.utils.cas.{CasAuthenticatingClient, CasClient, CasParams}
import org.http4s.Uri._
import org.http4s._
import org.http4s.client.Client

import scalaz.concurrent.Task

object EmailSender {
  def init(c: Config) = {
    val host = c.getString("hakuperusteet.cas.url")
    val username = c.getString("hakuperusteet.user")
    val password = c.getString("hakuperusteet.password")

    val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
    val casParams = CasParams("/ryhmasahkoposti-service", username, password)
    val emailClient = new EmailClient(host, new CasAuthenticatingClient(casClient, casParams, org.http4s.client.blaze.defaultClient))
    new EmailSender(emailClient)
  }
}

class EmailSender(emailClient: EmailClient) extends LazyLogging {
  implicit val formats = fi.vm.sade.hakuperusteet.formatsHenkilo

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
