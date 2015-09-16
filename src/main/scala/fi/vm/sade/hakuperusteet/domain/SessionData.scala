package fi.vm.sade.hakuperusteet.domain

case class SessionData(session: Session, user: Option[User], shouldPay: Option[Boolean], payment: List[Payment])
