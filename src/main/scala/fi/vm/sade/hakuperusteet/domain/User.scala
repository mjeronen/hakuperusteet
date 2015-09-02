package fi.vm.sade.hakuperusteet.domain

import java.util.Date

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: String, lastName: String, birthDate: Date,
                personId: Option[String], idpentityid: String, gender: String, nationality: String,
                educationLevel: String, educationCountry: String)

object User{
  def empty(email: String) = User(None, None,email, "", "", new Date(), None, "", "", "", "", "")
}
