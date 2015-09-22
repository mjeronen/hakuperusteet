package fi.vm.sade.hakuperusteet.email

import java.io.{StringWriter, StringReader}
import com.github.mustachejava.DefaultMustacheFactory

object EmailTemplate {
  private val welcomeTemplate = compileMustache("/email/welcome.mustache")
  private val receiptTemplate = compileMustache("/email/receipt.mustache")

  def renderWelcome(email: String) = {
    val sw = new StringWriter()
    welcomeTemplate.execute(sw, WelcomeValues(email))
    sw.toString
  }

  def renderReceipt(values: ReceiptValues) = {
    val sw = new StringWriter()
    receiptTemplate.execute(sw, values)
    sw.toString
  }

  private def compileMustache(templateUrl: String) = {
    val templateString = io.Source.fromInputStream(getClass.getResourceAsStream(templateUrl)).mkString
    new DefaultMustacheFactory().compile(new StringReader(templateString), templateUrl)
  }
}

case class WelcomeValues(email: String)
case class ReceiptValues(validForallSimilarApplicationsUntil: String, amount: String, reference: String)
