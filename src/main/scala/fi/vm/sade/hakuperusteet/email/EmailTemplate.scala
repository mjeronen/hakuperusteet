package fi.vm.sade.hakuperusteet.email

import java.io.{StringWriter, StringReader}
import com.github.mustachejava.DefaultMustacheFactory

object EmailTemplate {
  private val templateUrl = "/email/email.mustache"
  private val templateString = io.Source.fromInputStream(getClass.getResourceAsStream(templateUrl)).mkString
  private val mustache = new DefaultMustacheFactory().compile(new StringReader(templateString), templateUrl)

  case class TemplateValues(validForallSimilarApplicationsUntil: String)

  def render(validForallSimilarApplicationsUntil: String) = {
    val sw = new StringWriter()
    mustache.execute(sw, TemplateValues(validForallSimilarApplicationsUntil))
    sw.toString
  }


  def main(args: Array[String]) {
    EmailSender.send("jussi.jartamo@gofore.com", "Payment", render("22.11.2015"))
  }
}
