package fi.vm.sade.hakuperusteet.tarjonta

import java.net.URL

import com.typesafe.config.Config
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._

case class ApplicationObject(name: String, providerName: String, baseEducations: List[String], description: String)

case class Tarjonta(tarjontaBaseUrl: String) {
  implicit val formats = Serialization.formats(NoTypeHints)

  def getApplicationObject(hakukohdeOid: String) = Option(read[Result](urlToString(tarjontaBaseUrl + "/" + hakukohdeOid)))
    .map(r => r.result)
    .map(r => ApplicationObject(r.hakukohteenNimet.kieli_en, r.tarjoajaNimet.en, r.hakukelpoisuusvaatimusUris, r.lisatiedot.kieli_en)).get

  private def urlToString(url: String) = io.Source.fromInputStream(new URL(url).openStream()).mkString
}

object Tarjonta {
  def init(p: Config) = Tarjonta(p.getString("tarjonta.application.object.url"))
}

private case class Result(result: Hakukohde)
private case class Nimi(kieli_en: String)
private case class Nimi2(en: String)
private case class Hakukohde(tarjoajaNimet: Nimi2, hakukohteenNimet: Nimi, hakukelpoisuusvaatimusUris: List[String], lisatiedot: Nimi)
