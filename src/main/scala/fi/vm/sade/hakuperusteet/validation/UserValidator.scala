package fi.vm.sade.hakuperusteet.validation

import fi.vm.sade.hakuperusteet.util.ValidationUtil

import fi.vm.sade.hakuperusteet.domain.User
import fi.vm.sade.hakuperusteet.domain.User._
import fi.vm.sade.hakuperusteet.koodisto.{Languages, Countries}
import scalaz._
import scalaz.syntax.applicative._
import scalaz.syntax.validation._
import fi.vm.sade.hakuperusteet.util.{ValidationUtil}

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
      User(None, None, _:String, firstName, lastName, java.sql.Date.valueOf(birthDate), personId, _:String, gender, nativeLanguage, nationality)
    }
  }

  def parseUserData(params: Params): ValidationResult[User] = {
    (parseOptionalInt("id")(params)
      |@| parseOptional("personOid")(params)
      |@| parseNonEmpty("email")(params)
      |@| parseNonEmpty("firstName")(params)
      |@| parseNonEmpty("lastName")(params)
      |@| parseExists("birthDate")(params).flatMap(parseLocalDate)
      |@| parseOptionalPersonalId(params)
      |@| parseNonEmpty("idpentityid")(params)
      |@| parseExists("gender")(params).flatMap(validateGender)
      |@| parseExists("nativeLanguage")(params).flatMap(validateNativeLanguage)
      |@| parseExists("nationality")(params).flatMap(validateCountry)
      ) { (id, personOid, email, firstName, lastName, birthDate, personId, idpentityid, gender, nativeLanguage, nationality) =>
      User(id, personOid, email, firstName, lastName, java.sql.Date.valueOf(birthDate), personId, idpentityid, gender, nativeLanguage, nationality)
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
