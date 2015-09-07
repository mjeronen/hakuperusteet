package fi.vm.sade.hakuperusteet.koodisto

import java.net.URL

import fi.vm.sade.hakuperusteet.Configuration
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.{NoTypeHints, DefaultFormats, Formats}
import org.json4s.native.Serialization.{read, write}

object Countries {
  implicit val formats = Serialization.formats(NoTypeHints)

  lazy val countries = read[List[Country]](urlToString("koodisto.countries.url"))
  lazy val eeaCountries: List[String] = read[Valtioryhma](urlToString("koodisto.eea.countries.url")).withinCodeElements
    .map(_.codeElementValue)

  private def urlToString(url: String) = io.Source.fromInputStream(new URL(
    Configuration.props.getString(url)).openStream()).mkString

}

case class Metadata(nimi: String, kieli: String)
case class Country(koodiArvo: String, metadata: List[Metadata])

private case class Element(codeElementValue: String)
private case class Valtioryhma(withinCodeElements: List[Element])
