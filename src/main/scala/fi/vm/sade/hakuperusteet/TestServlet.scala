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
import scalaz.syntax.validation._
import scalaz.syntax.applicative._


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
      signature.update(Seq(firstName, lastName, birthDate.toString, email, shouldPay, hasPaid)
        mkString ("") getBytes ("UTF-8"))
      signature.sign()
    }
  }

  private def parseCheckbox(key: String, params: Map[String, String]): ValidationNel[String, Boolean] =
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
    (parseBirthDate(params("birth-date"))
      |@| parseCheckbox("should-pay", params)
      |@| parseCheckbox("has-paid", params)
      ) { (birthDate: LocalDate, shouldPay: Boolean, hasPaid: Boolean) =>
      Parameters(
        params("first-name"),
        params("last-name"),
        birthDate,
        params("email"),
        shouldPay,
        hasPaid
      )
    }

  get("/") {
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
          "hash" -> Base64.getEncoder.encodeToString(parameters.sign(key))
        ) mapValues (URLEncoder.encode(_, "UTF-8"))
        halt(status = 303, headers = Map("Location" -> url(params("url"), query, false, false, false)))
      })
  }
}
