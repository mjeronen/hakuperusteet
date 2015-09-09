package fi.vm.sade.hakuperusteet.tarjonta

import java.net.URL

import com.typesafe.config.Config
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class ApplicationObject(name: String, providerName: String, baseEducations: List[String], description: String)

object Tarjonta {
  implicit val formats = Serialization.formats(NoTypeHints)

  def init(p: Config) = Option(read[Result](urlToString("tarjonta.application.object.1.2.246.562.20.69046715533",p)))
    .map(r => r.result)
    .map(r => ApplicationObject(r.hakukohteenNimet.kieli_en, r.tarjoajaNimet.en, r.hakukelpoisuusvaatimusUris, r.lisatiedot.kieli_en)).get


  private def urlToString(url: String, props: Config) = io.Source.fromInputStream(new URL(
    props.getString(url)).openStream()).mkString

}

private case class Result(result: Hakukohde)
private case class Nimi(kieli_en: String)
private case class Nimi2(en: String)
private case class Hakukohde(tarjoajaNimet: Nimi2, hakukohteenNimet: Nimi, hakukelpoisuusvaatimusUris: List[String], lisatiedot: Nimi)
