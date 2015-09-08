package fi.vm.sade.hakuperusteet.koodisto

import java.net.URL

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.Configuration
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.{NoTypeHints, DefaultFormats, Formats}
import org.json4s.native.Serialization.{read, write}

case class Countries(countries: List[Country], eeaCountries: List[String]) {
  def shouldPay(educationCountry: String) = !eeaCountries.contains(educationCountry)
}

object Countries {

  def init(props: Config) = Countries(countries(props),eeaCountries(props).withinCodeElements.map(_.codeElementValue))

  implicit val formats = Serialization.formats(NoTypeHints)

  private def countries(p: Config) = read[List[Country]](urlToString("koodisto.countries.url",p))
  private def eeaCountries(p: Config) = read[Valtioryhma](urlToString("koodisto.eea.countries.url",p))

  private def urlToString(url: String, props: Config) = io.Source.fromInputStream(new URL(
    props.getString(url)).openStream()).mkString

}

case class Metadata(nimi: String, kieli: String)
case class Country(koodiArvo: String, metadata: List[Metadata])

private case class Element(codeElementValue: String)
private case class Valtioryhma(withinCodeElements: List[Element])
