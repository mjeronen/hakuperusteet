package fi.vm.sade.hakuperusteet.koodisto

import java.net.URL

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.Configuration
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import org.json4s.{NoTypeHints, DefaultFormats, Formats}
import org.json4s.native.Serialization.{read, write}

case class Languages(languages: List[SimplifiedLanguage])

case class SimplifiedLanguage(id: String, name: String)

private case class Metadata2(nimi: String, kieli: String)
private case class Language(koodiArvo: String, metadata: List[Metadata2])

object Languages {

  def init(props: Config) = Languages(languages(props))

  implicit val formats = Serialization.formats(NoTypeHints)

  private def languages(p: Config) = read[List[Language]](urlToString("koodisto.languages.url",p))
    .filter(l => l.metadata.exists(_.kieli.equals("EN")))
    .map(c => SimplifiedLanguage(c.koodiArvo,c.metadata.find(_.kieli.equals("EN")).get.nimi))
    .sortWith((c0,c1) => c0.name.compareTo(c1.name) < 0)

  private def urlToString(url: String, props: Config) = io.Source.fromInputStream(new URL(
    props.getString(url)).openStream()).mkString

}
