package fi.vm.sade.hakuperusteet.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import fi.vm.sade.hakuperusteet._
import fi.vm.sade.utils.validator.HenkilotunnusValidator
import fi.vm.sade.utils.validator.InputNameValidator

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

  def parseValidName(key: String)(params: Params) = parseNonEmpty(key)(params)
    .flatMap(a => InputNameValidator.validate(a))

  def parseOptional(key: String)(params: Params) = params.get(key) match { case e => e.successNel }

  def parseOptionalInt(key: String)(params: Params) = (params.get(key): @unchecked) match { case Some(i) => Try(Option(i.toInt).successNel).recover{
    case e => e.getMessage.failureNel
  }.get}

  def parseLocalDate(input: String): ValidationResult[LocalDate] =
    Try(LocalDate.parse(input, DateTimeFormatter.ofPattern("ddMMyyyy")).successNel).recover {
      case e => e.getMessage.failureNel
    }.get

  def parseOptionalPersonalId(params: Params): ValidationResult[Option[String]] =
    (params.get("birthDate"), params.get("personId")) match {
      case (Some(b), Some(p)) =>
        parseLocalDate(b) match {
          case scalaz.Success(birthDateParsed) =>
            val pid = birthDateParsed.format(personIdDateFormatter) + p
            HenkilotunnusValidator.validate(pid) match {
              case scalaz.Success(a) => Some(pid).successNel
              case scalaz.Failure(e) => s"invalid pid $pid - [${e.stream.mkString(",")}]".failureNel
            }
          case scalaz.Failure(e) => s"invalid birthDate $b".failureNel
        }
      case _ => None.successNel
    }

}
