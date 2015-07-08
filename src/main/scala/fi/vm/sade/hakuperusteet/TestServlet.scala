package fi.vm.sade.hakuperusteet

import java.security.interfaces.RSAPrivateKey
import java.security.{PrivateKey, Signature}
import java.util.Base64

import com.netaporter.uri.Uri
import com.netaporter.uri.config.UriConfig
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.scalatra._
import org.slf4j.LoggerFactory

import scala.util.Try
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._


class TestServlet(key: RSAPrivateKey) extends ScalatraServlet {

  val logger = LoggerFactory.getLogger(this.getClass)

  type ValidationResult[A] = ValidationNel[String, A]

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

  private def parseExists(key: String)(params: Map[String, String]): ValidationResult[String] =
    params.get(key).map(_.successNel).getOrElse(s"Parameter $key does not exist".failureNel)

  private def parseNonEmpty(key: String)(params: Map[String, String]): ValidationResult[String] =
    parseExists(key)(params)
      .flatMap(a => if (a.nonEmpty) a.successNel else s"Parameter $key is empty".failureNel)

  private def parseCheckbox(key: String)(params: Map[String, String]): ValidationResult[Boolean] =
    params.contains(key).successNel

  private def parseLocalDate(input: String): ValidationResult[LocalDate] =
    Try(ISODateTimeFormat.date().parseLocalDate(input).successNel) recover {
      case e: IllegalArgumentException => e.getMessage.failureNel
    } get

  private def parseParameters(params: Map[String, String]): ValidationResult[Parameters] = {
    (parseNonEmpty("first-name")(params)
      |@| parseNonEmpty("last-name")(params)
      |@| parseExists("birth-date")(params).flatMap(parseLocalDate)
      |@| parseNonEmpty("email")(params)
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
  }

  private def parseUri(params: Map[String, String]): ValidationResult[Uri] =
    parseExists("url")(params)
      .flatMap (s => Try(Uri.parse(s).successNel)
      .getOrElse (s"Not a valid url $s".failureNel))

  private def parseSignedUri(params: Map[String, String]): ValidationResult[Uri] =
    (parseParameters(params) |@| parseUri(params)) { (parameters, uri) =>
      uri.addParams(Seq(
        "first-name" -> parameters.firstName,
        "last-name" -> parameters.lastName,
        "birth-date" -> parameters.birthDate.toString,
        "email" -> parameters.email,
        "should-pay" -> parameters.shouldPay.toString,
        "has-paid" -> parameters.hasPaid.toString,
        "signature" -> Base64.getEncoder.encodeToString(parameters.sign(key))
      ))
    }

  post("/") {
    parseSignedUri(params) bitraverse (
      errors => {
        contentType = "application/json"
        halt(status = 400, body = compact(render("errors" -> errors.list)))
      },
      uri => halt(status = 303, headers = Map("Location" -> uri.toString))
      )
  }
}
