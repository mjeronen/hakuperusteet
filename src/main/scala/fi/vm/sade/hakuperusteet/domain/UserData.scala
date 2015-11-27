package fi.vm.sade.hakuperusteet.domain

case class UserData(user: User, applicationObject: Seq[ApplicationObject], payments: Seq[Payment])

case class PartialUserData(user: PartialUser, payments: Seq[Payment], isPartialUserData: Boolean = true)

