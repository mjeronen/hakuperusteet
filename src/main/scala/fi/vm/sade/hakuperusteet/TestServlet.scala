package fi.vm.sade.hakuperusteet

import java.net.URLEncoder
import java.security.interfaces.RSAPrivateKey
import java.security.{PrivateKey, Signature}
import java.util.Base64

import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatra._
import org.slf4j.LoggerFactory

import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._


class TestServlet(key: RSAPrivateKey) extends ScalatraServlet {

  val logger = LoggerFactory.getLogger(this.getClass)

  case class Parameters(firstName: String,
                        lastName: String,
                        birthDate: LocalDate,
                        email: String,
                        shouldPay: Boolean,
                        hasPaid: Boolean) {
    def sign(key: PrivateKey): Array[Byte] = {
      val signature = Signature.getInstance("SHA256withRSA")
      signature.initSign(key)
      signature.update(Seq(firstName, lastName, birthDate, email, shouldPay, hasPaid) mkString ("") getBytes ("UTF-8"))
      signature.sign()
    }
  }

  private def parseNonEmpty(key: String)(input: String): ValidationNel[String, String] =
    if (input.nonEmpty) input.successNel
    else ("Empty parameter: " + key).failureNel

  private def parseCheckbox(key: String)(params: Map[String, String]): ValidationNel[String, Boolean] =
    if (params.contains(key)) true.successNel
    else false.successNel

  private def parseBirthDate(input: String): ValidationNel[String, LocalDate] =
    try {
      ISODateTimeFormat.date().parseLocalDate(input).successNel
    }
    catch {
      case ex: IllegalArgumentException => ex.getMessage.failureNel
    }

  private def parseParameters(params: Map[String, String]): ValidationNel[String, Parameters] =
    (parseNonEmpty("first-name")(params("first-name"))
      |@| parseNonEmpty("last-name")(params("last-name"))
      |@| parseBirthDate(params("birth-date"))
      |@| parseNonEmpty("email")(params("email"))
      |@| parseCheckbox("should-pay")(params)
      |@| parseCheckbox("has-paid")(params)
      ) { (firstName, lastName, birthDate, email, shouldPay, hasPaid) =>
      Parameters(
        firstName,
        lastName,
        birthDate,
        email,
        shouldPay,
        hasPaid
      )
    }

  post("/") {
    parseParameters(params).bitraverse(
      errors => {
        contentType = "application/json"
        halt(status = 400, body = compact(render("errors" -> errors.list)))
      },
      parameters => {
        val query = Map(
          "first-name" -> parameters.firstName,
          "last-name" -> parameters.lastName,
          "birth-date" -> parameters.birthDate.toString,
          "email" -> parameters.email,
          "should-pay" -> parameters.shouldPay.toString,
          "has-paid" -> parameters.hasPaid.toString,
          "signature" -> Base64.getEncoder.encodeToString(parameters.sign(key))
        ) mapValues (URLEncoder.encode(_, "UTF-8"))
        halt(status = 303, headers = Map("Location" -> url(params("url"), query, false, false, false)))
      })
  }
}
