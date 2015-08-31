package fi.vm.sade.hakuperusteet

import java.util.Date

case class User(personOid: Option[String], email: String, firstName: String, lastName: String, birthDate: Date,
                personId: Option[String], idpentityid: String, gender: String, nationality: String,
                educationLevel: String, educationCountry: String)

object User{
  def empty(email: String) = User(None,email, "", "", new Date(), None, "", "", "", "", "")
}
