package fi.vm.sade.hakuperusteet.hakuapp

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.{PaymentUpdate, Henkilo, User, PaymentStatus}
import fi.vm.sade.hakuperusteet.henkilo.IdpUpsertRequest
import fi.vm.sade.hakuperusteet.util.CasClientUtils
import fi.vm.sade.utils.cas.{CasAuthenticatingClient, CasParams, CasClient}
import org.http4s.Uri._
import org.http4s.{Uri, Method, Request}
import org.http4s.client.Client
import fi.vm.sade.hakuperusteet.domain.PaymentState.PaymentState

object HakuAppClient {
  def init(c: Config) = {
    val host = c.getString("hakuperusteet.cas.url")
    val username = c.getString("hakuperusteet.user")
    val password = c.getString("hakuperusteet.password")
    val casClient = new CasClient(host, org.http4s.client.blaze.defaultClient)
    val casParams = CasParams("/authentication-service", username, password)
    new HakuAppClient(host, new CasAuthenticatingClient(casClient, casParams, org.http4s.client.blaze.defaultClient))
  }
}

class HakuAppClient(hakuAppServerUrl: String, client: Client) extends LazyLogging with CasClientUtils {
  import fi.vm.sade.hakuperusteet._

  def updateHakemusWithPaymentStatus(hakemusOid: String, status: PaymentState) = client.prepare(req(hakemusOid, status)).run

  private def req(hakemusOid: String, paymentStatus: PaymentState) = Request(
    method = Method.POST,
    uri = resolve(urlToUri(hakuAppServerUrl), Uri(path = s"/haku-app/applications/$hakemusOid/updatePaymentStatus"))
  ).withBody(PaymentUpdate(paymentStatus))(json4sEncoderOf[PaymentUpdate])
}
