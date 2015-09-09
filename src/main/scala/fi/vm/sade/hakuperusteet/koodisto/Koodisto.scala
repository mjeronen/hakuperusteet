package fi.vm.sade.hakuperusteet.koodisto

import java.net.URL

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.Configuration
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class SimplifiedCode(id: String, name: String)

case class Countries(countries: List[SimplifiedCode], eeaCountries: List[String]) {
  def shouldPay(educationCountry: String) = !eeaCountries.contains(educationCountry)
}
case class Languages(languages: List[SimplifiedCode])
case class Educations(educations: List[SimplifiedCode])

object Koodisto {
  implicit val formats = Serialization.formats(NoTypeHints)

  def initCountries(props: Config) = Countries(countries(props),eeaCountries(props).withinCodeElements.map(_.codeElementValue))

  def initLanguages(props: Config) = Languages(languages(props))

  def initBaseEducation(props: Config) = Educations(educations(props))

  private def educations(p: Config) = read[List[Koodi]](urlToString("koodisto.base.education.url",p))
    .filter(l => l.metadata.exists(_.kieli.equals("EN")))
    .map(c => SimplifiedCode(c.koodiArvo,c.metadata.find(_.kieli.equals("EN")).get.nimi))
    .sortWith((c0,c1) => c0.name.compareTo(c1.name) < 0)

  private def languages(p: Config) = read[List[Koodi]](urlToString("koodisto.languages.url",p))
    .filter(l => l.metadata.exists(_.kieli.equals("EN")))
    .map(c => SimplifiedCode(c.koodiArvo,c.metadata.find(_.kieli.equals("EN")).get.nimi))
    .sortWith((c0,c1) => c0.name.compareTo(c1.name) < 0)

  private def countries(p: Config) = read[List[Koodi]](urlToString("koodisto.countries.url",p))
    .map(c => SimplifiedCode(c.koodiArvo,c.metadata.find(_.kieli.equals("EN")).get.nimi))
    .sortWith((c0,c1) => c0.name.compareTo(c1.name) < 0)

  private def eeaCountries(p: Config) = read[Valtioryhma](urlToString("koodisto.eea.countries.url",p))

  private def urlToString(url: String, props: Config) = io.Source.fromInputStream(new URL(
    props.getString(url)).openStream()).mkString

}
private case class Metadata(nimi: String, kieli: String)
private case class Koodi(koodiArvo: String, metadata: List[Metadata])

private case class Element(codeElementValue: String)
private case class Valtioryhma(withinCodeElements: List[Element])
