package fi.vm.sade.hakuperusteet

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import scala.collection.JavaConversions._
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload

object GoogleVerifier {

  val client_id =
    Configuration.props.getString("google.authentication.client.id")

  val verifier = new GoogleIdTokenVerifier.Builder(
    GoogleNetHttpTransport.newTrustedTransport, JacksonFactory.getDefaultInstance)
    .setAudience(List(client_id))
    .build()

  def verify(token: String): Boolean = verifier.verify(token).getPayload.getAuthorizedParty == client_id
}
