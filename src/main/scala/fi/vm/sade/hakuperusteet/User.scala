package fi.vm.sade.hakuperusteet

case class User(personOid: Option[String], email: String, firstName: String, lastName: String, birthDate: String,
                personId: Option[String],
                idpentityid: String,
                gender: String, nationality: String)

object User{
  def empty(email: String) = User(None,email, "", "", "", None, "", "", "")
}
