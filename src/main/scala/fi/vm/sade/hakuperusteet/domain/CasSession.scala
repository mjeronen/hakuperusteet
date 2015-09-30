package fi.vm.sade.hakuperusteet.domain

case class CasSession(id: Option[Int], username: String, roles: List[String])
