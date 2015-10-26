package fi.vm.sade.hakuperusteet.admin.auth

import scala.xml.Utility

object CasLogout {

  def parseTicketFromLogoutRequest(logoutRequest: String): Option[String] = {
    Utility.trim(scala.xml.XML.loadString(logoutRequest)) match {
      case <samlp:LogoutRequest><saml:NameID>{nameID}</saml:NameID><samlp:SessionIndex>{ticket}</samlp:SessionIndex></samlp:LogoutRequest> =>
        Some(ticket.text)
      case _ => None
    }
  }
}
