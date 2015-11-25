package fi.vm.sade.hakuperusteet.validation

import fi.vm.sade.hakuperusteet.domain.{IDPEntityId, User}
import fi.vm.sade.hakuperusteet.koodisto.{Countries, Languages}
import fi.vm.sade.hakuperusteet.util.ValidationUtil

import scalaz.syntax.applicative._
import scalaz.syntax.validation._

case class UserValidator(countries: Countries, languages: Languages) extends ValidationUtil {

  def parseUserDataWithoutEmailAndIdpentityid(params: Params): ValidationResult[(String,String) => User] = {
    (parseNonEmpty("firstName")(params)
      |@| parseNonEmpty("lastName")(params)
      |@| parseExists("birthDate")(params).flatMap(parseLocalDate)
      |@| parseOptionalPersonalId(params)
      |@| parseExists("gender")(params).flatMap(validateGender)
      |@| parseExists("nativeLanguage")(params).flatMap(validateNativeLanguage)
      |@| parseExists("nationality")(params).flatMap(validateCountry)
      ) { (firstName, lastName, birthDate, personId, gender, nativeLanguage, nationality) =>
      (email: String, idpEntityId: String) => User(None, None, email, Some(firstName), Some(lastName),
        Some(java.sql.Date.valueOf(birthDate)), personId, IDPEntityId.withName(idpEntityId),
        Some(gender), Some(nativeLanguage), Some(nationality))
    }
  }

  def parseUserData(params: Params): ValidationResult[User] = {
    (parseOptionalInt("id")(params)
      |@| parseOptional("personOid")(params)
      |@| parseNonEmpty("email")(params)
      |@| parseValidName("firstName")(params)
      |@| parseValidName("lastName")(params)
      |@| parseExists("birthDate")(params).flatMap(parseLocalDate)
      |@| parseOptionalPersonalId(params)
      |@| parseIDPEntityId(params)
      |@| parseExists("gender")(params).flatMap(validateGender)
      |@| parseExists("nativeLanguage")(params).flatMap(validateNativeLanguage)
      |@| parseExists("nationality")(params).flatMap(validateCountry)
      ) { (id, personOid, email, firstName, lastName, birthDate, personId, idpentityid, gender, nativeLanguage, nationality) =>
      User(id, personOid, email, Some(firstName), Some(lastName), Some(java.sql.Date.valueOf(birthDate)), personId, idpentityid, Some(gender), Some(nativeLanguage), Some(nationality))
    }
  }

  def parseEmailToken(params: Params): ValidationResult[(String, String)] = {
    (parseNonEmpty("email")(params).flatMap(validateEmail) |@| parseExists("hakukohdeOid")(params)) { (email, hakukohdeOid) => (email, hakukohdeOid) }
  }

  private def validateGender(gender: String): ValidationResult[String] =
    if (gender == "1" || gender == "2") gender.successNel
    else  s"gender value $gender is invalid".failureNel

  private def validateNativeLanguage(nativeLanguage: String): ValidationResult[String] =
    if (languages.languages.map(_.id).contains(nativeLanguage)) nativeLanguage.successNel
    else s"unknown nativeLanguage $nativeLanguage".failureNel

  private def validateCountry(nationality: String): ValidationResult[String] =
    if (countries.countries.map(_.id).contains(nationality)) nationality.successNel
    else s"unknown country $nationality".failureNel

  private def validateEmail(email: String): ValidationResult[String] =
    if (!email.isEmpty && email.contains("@") && !email.contains(" ") && !email.contains(",") && !email.contains("\t")) email.successNel
    else s"invalid email $email".failureNel
}
