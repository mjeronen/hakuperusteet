package fi.vm.sade.hakuperusteet.domain

case class SessionData(session: Session, user: Option[User], applicationObject: List[ApplicationObject], payment: List[Payment])
