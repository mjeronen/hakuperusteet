package fi.vm.sade.hakuperusteet.domain

case class SessionData(user: User, payment: Option[Payment])
