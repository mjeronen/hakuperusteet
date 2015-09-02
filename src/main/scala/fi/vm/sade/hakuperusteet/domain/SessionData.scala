package fi.vm.sade.hakuperusteet.domain

case class SessionData(user: Option[User], payment: Option[Payment])
