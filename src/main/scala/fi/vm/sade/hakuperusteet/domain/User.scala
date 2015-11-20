package fi.vm.sade.hakuperusteet.domain

import java.util.Date

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: Option[String], lastName: Option[String], birthDate: Option[Date],
                personId: Option[String], idpentityid: String, gender: Option[String], nativeLanguage: Option[String], nationality: Option[String]) {
  def fullName = s"$firstName $lastName"
}

object User {
  def partialUser(id: Option[Int], personOid: Option[String], email: String, idpentityid: String) = {
    User(id, personOid, email, None, None, None, None, idpentityid, None, None, None)
  }
}