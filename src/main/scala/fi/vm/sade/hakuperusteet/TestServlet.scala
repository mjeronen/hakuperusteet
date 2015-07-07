package fi.vm.sade.hakuperusteet

import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64

import fi.vm.sade.hakuperusteet.Types.Oid
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.scalatra._
import org.slf4j.LoggerFactory

import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._


class TestServlet(secrets: Map[Oid, String]) extends ScalatraServlet {

  val logger = LoggerFactory.getLogger(this.getClass)

  private def sha256(input: String): String =
    Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(input.getBytes("UTF-8")))

  case class Parameters(name: String, birthDate: LocalDate, mail: String, oid: Oid) {
    def hash(secret: String): String =
      sha256(Seq(name, birthDate.toString, mail, oid, secret) mkString)
  }

  private def validateBirthDate(input: String): ValidationNel[String, LocalDate] =
    try { ISODateTimeFormat.date().parseLocalDate(input).successNel }
    catch { case ex: IllegalArgumentException => ex.getMessage.failureNel }

  private def validateOid(input: String): ValidationNel[String, Oid] =
    if (input.nonEmpty) input.successNel
    else "Empty oid".failureNel

  private def parseParameters(params: Map[String, String]): ValidationNel[String, Parameters] =
    (validateBirthDate(params("birth-date")) |@| validateOid(params("oid"))) { (birthDate, oid) =>
      Parameters(params("name"), birthDate, params("mail"), oid)
    }

  get("/") {
    parseParameters(params).bitraverse(
      errors => {
        contentType = "application/json"
        halt(status = 400, body = compact(render("errors" -> errors.list)))
      },
      parameters => {
        val query = Map(
          "name" -> parameters.name,
          "birth-date" -> parameters.birthDate.toString,
          "mail" -> parameters.mail,
          "oid" -> parameters.oid,
          "hash" -> parameters.hash(secrets get parameters.oid get)
        ) mapValues (URLEncoder.encode(_, "UTF-8"))
        halt(status = 303, headers = Map("Location" -> url(params("url"), query, false, false, false)))
      })
  }
}
