package fi.vm.sade.hakuperusteet.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try
import scalaz._
import scalaz.syntax.validation._

trait ValidationUtil {
  type ValidationResult[A] = ValidationNel[String, A]
  type Params = Map[String, String]

  def parseExists(key: String)(params: Params) = params.get(key).map(_.successNel)
    .getOrElse(s"Parameter $key does not exist".failureNel)

  def parseNonEmpty(key: String)(params: Params) = parseExists(key)(params)
    .flatMap(a => if (a.nonEmpty) a.successNel else s"Parameter $key is empty".failureNel)

  def parseOptional(key: String)(params: Params) = params.get(key) match { case e => e.successNel }

  def parseLocalDate(input: String): ValidationResult[LocalDate] =
    Try(LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE).successNel).recover {
      case e => e.getMessage.failureNel
    }.get
}
