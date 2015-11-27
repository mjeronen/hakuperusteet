package fi.vm.sade.hakuperusteet.domain

import java.util.Date

import fi.vm.sade.hakuperusteet.domain.IDPEntityId.IDPEntityId

object IDPEntityId extends Enumeration {
  type IDPEntityId = Value
  val google, oppijaToken = Value
}
trait AbstractUser {
  def email: String
  def fullName: String
  def personOid: Option[String]
  def uiLang: String
}

case class PartialUser(id: Option[Int], personOid: Option[String], email: String, idpentityid: IDPEntityId, uiLang:String, partialUser: Boolean = true) extends AbstractUser {
  def fullName = email
}

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: Option[String], lastName: Option[String], birthDate: Option[Date],
                personId: Option[String], idpentityid: IDPEntityId, gender: Option[String], nativeLanguage: Option[String], nationality: Option[String],
                uiLang: String) extends AbstractUser {
  def fullName = s"$firstName $lastName"
}
