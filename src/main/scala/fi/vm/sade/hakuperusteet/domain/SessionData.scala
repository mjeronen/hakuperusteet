package fi.vm.sade.hakuperusteet.domain

case class SessionData(user: Option[User], shouldPay: Option[Boolean], payment: List[Payment])
