package fi.vm.sade.hakuperusteet

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.typesafe.scalalogging.slf4j.LazyLogging
import scala.collection.JavaConversions._
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload

object GoogleVerifier extends LazyLogging {

  val clientId = Configuration.props.getString("google.authentication.client.id")
  val hostedDomain = Configuration.props.getString("google.authentication.hosted.domain")

  val verifier = new GoogleIdTokenVerifier.Builder(
    GoogleNetHttpTransport.newTrustedTransport, JacksonFactory.getDefaultInstance)
    .setAudience(List(clientId))
    .build()

  def verify(token: String): Boolean = Option(verifier.verify(token)).map(_.getPayload)
    .filter(p => {
    logger.info("Hosted Domain {}", Option(p).map(_.getHostedDomain).getOrElse("'payload was null'"))
    true})
    .exists(p => p.getHostedDomain == hostedDomain && p.getAuthorizedParty == clientId)

}
