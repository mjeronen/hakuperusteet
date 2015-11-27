package fi.vm.sade.hakuperusteet.domain

import java.util.Date

import fi.vm.sade.hakuperusteet.domain.IDPEntityId.IDPEntityId

object IDPEntityId extends Enumeration {
  type IDPEntityId = Value
  val google, oppijaToken = Value
}

case class User(id: Option[Int], personOid: Option[String], email: String, firstName: Option[String], lastName: Option[String], birthDate: Option[Date],
                personId: Option[String], idpentityid: IDPEntityId, gender: Option[String], nativeLanguage: Option[String], nationality: Option[String],
                uiLang: String) {
  def fullName = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}"
}

object User {
  def partialUser(id: Option[Int], personOid: Option[String], email: String, idpentityid: IDPEntityId, uiLang:String) = {
    User(id, personOid, email, None, None, None, None, idpentityid, None, None, None, uiLang)
  }
}
