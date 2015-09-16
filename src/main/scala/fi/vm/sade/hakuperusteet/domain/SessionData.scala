package fi.vm.sade.hakuperusteet.domain

case class SessionData(email: String, user: Option[User], shouldPay: Option[Boolean], payment: List[Payment])
