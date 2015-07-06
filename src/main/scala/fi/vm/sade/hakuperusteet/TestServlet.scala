package fi.vm.sade.hakuperusteet

import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64

import fi.vm.sade.hakuperusteet.Types.Oid
import org.scalatra._
import org.slf4j.LoggerFactory


class TestServlet(secrets: Map[Oid, String]) extends ScalatraServlet {

  val logger = LoggerFactory.getLogger(this.getClass)

  get("/") {
    val secret = secrets get params("oid")
    val hashInput: String = Seq("name", "birth-date", "mail", "oid") map (params(_)) mkString ("") + secret.get
    val query = Map(
      "name" -> URLEncoder.encode(params("name"), "UTF-8"),
      "birth-date" -> URLEncoder.encode(params("birth-date"), "UTF-8"),
      "mail" -> URLEncoder.encode(params("mail"), "UTF-8"),
      "hash" -> sha256(hashInput)
    )
    halt(status = 303, headers = Map("Location" -> url(params("url"), query, false, false, false)))
  }

  private def sha256(input: String): String =
    Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(input.getBytes("UTF-8")))
}
