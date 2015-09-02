package fi.vm.sade.hakuperusteet.domain

case class Session(id: Option[Int], email: String, token: String, idpentityid: String)
