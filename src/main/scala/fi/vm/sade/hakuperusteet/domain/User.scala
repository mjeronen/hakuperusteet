package fi.vm.sade.hakuperusteet.domain

import java.util.Date

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: String, lastName: String, birthDate: Date,
                personId: Option[String], idpentityid: String, gender: String, nativeLanguage: String, nationality: String) {
  def fullName = s"$firstName $lastName"
}