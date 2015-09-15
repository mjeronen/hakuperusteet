package fi.vm.sade.hakuperusteet.oppijantunnistus

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.domain.Session

case class OppijanTunnistus(c: Config) extends LazyLogging {
  def validateToken(token: String, idpentityid: String) = {
    logger.info(s"Validating token $token and idpentityid $idpentityid")
    //todo: call oppijantunnistus
    Some(Session(None, "jussi.vesala@iki.fi", token, idpentityid))
  }
}

object OppijanTunnistus {
  def init(c: Config) = OppijanTunnistus(c)
}
