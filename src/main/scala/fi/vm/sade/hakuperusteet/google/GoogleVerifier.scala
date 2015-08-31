package fi.vm.sade.hakuperusteet.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.Configuration

import scala.collection.JavaConversions._

object GoogleVerifier extends LazyLogging {

  val clientId = Configuration.props.getString("google.authentication.client.id")
  val hostedDomain = Configuration.props.getString("google.authentication.hosted.domain")

  val verifier = new GoogleIdTokenVerifier.Builder(
    GoogleNetHttpTransport.newTrustedTransport, JacksonFactory.getDefaultInstance)
    .setAudience(List(clientId))
    .build()

  def verify(token: String): Boolean = Option(verifier.verify(token)).map(_.getPayload)
    .map(logAndReturnIdentity)
    //.exists(p => p.getHostedDomain == hostedDomain && p.getAuthorizedParty == clientId) // todo: fixme
    .exists(p => p.getAuthorizedParty == clientId)

  private def logAndReturnIdentity(p: Payload) = {
    logger.info("Hosted Domain {}", Option(p).map(_.getHostedDomain).getOrElse("'payload was null'"))
    p
  }
}
