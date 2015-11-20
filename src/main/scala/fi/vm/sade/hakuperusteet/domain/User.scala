package fi.vm.sade.hakuperusteet.domain

import java.util.Date

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: String, lastName: String, birthDate: Date,
                personId: Option[String], idpentityid: String, gender: String, nativeLanguage: String, nationality: String) {
  def fullName = s"$firstName $lastName"
}

object User {
  def partialUser(id: Option[Int], personOid: Option[String], email: String, idpentityid: String) = {
    User(id, personOid, email, null, null ,null, None, idpentityid, null, null, null)
  }
}