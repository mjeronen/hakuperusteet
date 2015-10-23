package fi.vm.sade.hakuperusteet.tarjonta

import java.util.Date

import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.util.ServerException
import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization._
import fi.vm.sade.hakuperusteet.util.HttpUtil._

case class ApplicationObject(hakukohdeOid: String, hakuOid: String, name: String, providerName: String, baseEducations: List[String], description: String, hakuaikaId: String)
case class ApplicationSystem(hakuOid: String, formUrl: String, maksumuuriKaytossa: Boolean, hakuaikas: List[HakuAika])
case class HakuAika(hakuaikaId: String, alkuPvm: Long, loppuPvm: Long)
case class EnrichedApplicationObject(hakukohdeOid: String, hakuOid: String, name: String, providerName: String, baseEducations: List[String], description: String, maksumuuriKaytossa: Boolean, startDate: Date, endDate: Date)

object EnrichedApplicationObject {
  def apply(ao: ApplicationObject, as: ApplicationSystem): EnrichedApplicationObject = {
    val currentHakuAika = as.hakuaikas.find( (p) => p.hakuaikaId == ao.hakuaikaId).getOrElse(throw ServerException(s"missing hakuaika ${ao.hakuaikaId} in haku ${ao.hakuOid}"))
    EnrichedApplicationObject(ao.hakukohdeOid, ao.hakuOid, ao.name, ao.providerName, ao.baseEducations, ao.description,
      as.maksumuuriKaytossa, new Date(currentHakuAika.alkuPvm), new Date(currentHakuAika.loppuPvm))
  }
}

case class Tarjonta(tarjontaBaseUrl: String) {
  implicit val formats = Serialization.formats(NoTypeHints)

  def getApplicationObject(hakukohdeOid: String) = hakukohdeToApplicationObject(read[Result](urlToString(tarjontaBaseUrl + "hakukohde/" + hakukohdeOid)).result)

  def getApplicationSystem(hakuOid: String) = hakuToApplicationSystem(read[Result2](urlToString(tarjontaBaseUrl + "haku/" + hakuOid)).result)

  def enrichHakukohdeWithHaku(ao: ApplicationObject) = EnrichedApplicationObject(ao, hakuToApplicationSystem(read[Result2](urlToString(tarjontaBaseUrl + "haku/" + ao.hakuOid)).result))

  private def tarjontaUrisToKoodis(tarjontaUri: List[String]) = tarjontaUri.map(_.split("_")(1))

  private def hakukohdeToApplicationObject(r: Hakukohde) = ApplicationObject(r.oid, r.hakuOid, r.hakukohteenNimet.kieli_en, r.tarjoajaNimet.en, tarjontaUrisToKoodis(r.hakukelpoisuusvaatimusUris), r.lisatiedot.kieli_en, r.hakuaikaId)
  private def hakuToApplicationSystem(r: Haku) = ApplicationSystem(r.oid, r.hakulomakeUri, r.maksumuuriKaytossa, r.hakuaikas)
}

object Tarjonta {
  def init(p: Config) = Tarjonta(p.getString("tarjonta.api.url"))
}

private case class Result(result: Hakukohde)
private case class Nimi(kieli_en: String)
private case class Nimi2(en: String)
private case class Hakukohde(oid: String, tarjoajaNimet: Nimi2, hakukohteenNimet: Nimi, hakukelpoisuusvaatimusUris: List[String], lisatiedot: Nimi, hakuOid: String, hakuaikaId: String)

private case class Result2(result: Haku)
private case class Haku(oid: String, hakulomakeUri: String, maksumuuriKaytossa: Boolean, hakuaikas: List[HakuAika])
