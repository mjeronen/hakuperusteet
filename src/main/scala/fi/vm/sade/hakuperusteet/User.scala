package fi.vm.sade.hakuperusteet

case class User(oid: Option[Oid], email: String, firstName: String, lastName: String, birthDate: String, personId: Option[String],
                gender: String, nationality: String)
