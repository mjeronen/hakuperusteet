package fi.vm.sade.hakuperusteet.email

import java.io.{StringWriter, StringReader}
import com.github.mustachejava.{Mustache, DefaultMustacheFactory}

object EmailTemplate {
  private val welcomeTemplate: Mustache = compileMustache("/email/welcome.mustache")
  private val receiptTemplates = Map("fi" -> compileMustache("/email/receipt_fi.mustache"),
    "en" -> compileMustache("/email/receipt_en.mustache"),
    "sv" -> compileMustache("/email/receipt_sv.mustache"))
  val receiptTitles = Map("en" -> "Studyinfo: Your payment has been received",
    "fi" -> "Opintopolku - maksu vastaanotettu",
    "sv" -> "Studieinfo - betalningen har mottagits")
  def renderWelcome(values: WelcomeValues) = {
    val sw = new StringWriter()
    welcomeTemplate.execute(sw, values)
    sw.toString
  }

  def renderReceipt(values: ReceiptValues, lang: String) = {
    val sw = new StringWriter()
    receiptTemplates.getOrElse(lang,
      throw new RuntimeException(s"Email receipt with unknown lanuage $lang! Supported languages are 'fi','sv' and 'en'.")).execute(sw, values)
    sw.toString
  }

  private def compileMustache(templateUrl: String) = {
    val templateString = io.Source.fromInputStream(getClass.getResourceAsStream(templateUrl)).mkString
    new DefaultMustacheFactory().compile(new StringReader(templateString), templateUrl)
  }
}

case class WelcomeValues(fullName: String)
case class ReceiptValues(fullName: String, amount: String, reference: String)
