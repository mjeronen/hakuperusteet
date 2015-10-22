package fi.vm.sade.hakuperusteet.auth

import javax.servlet.http.{HttpServletRequest, HttpSession}

import fi.vm.sade.hakuperusteet.HakuperusteetServlet
import fi.vm.sade.hakuperusteet.domain.Session

trait SimpleAuth {
  def authenticate(request: HttpServletRequest): Option[Session]
}

trait JavaEESessionAuthentication { self: HakuperusteetServlet =>
  val sessionAuthKey = "SessionAuth"

  def isAuthenticated = {
    val session = request.getSession(false)
    session != null && session.getAttribute(sessionAuthKey) != null
  }

  def authenticate() = {
    if(!isAuthenticated) { // TODO APO REMOVE when front calls authenticate only when needed
      val matchedSessions: List[Session] = List(new GoogleBasicAuthStrategy(configuration, db, googleVerifier), new TokenAuthStrategy(configuration, db, oppijanTunnistus)).map( a => a.authenticate(request)).flatten
      if(matchedSessions.nonEmpty) {
        request.getSession.setAttribute(sessionAuthKey, matchedSessions.head)
      }
    }
  }

  def logOut() = {
    val session: HttpSession = request.getSession(false)
    if(session != null) {
      session.invalidate()
    }
  }

  def user:Session = {
    val session: HttpSession = request.getSession(false)
    if(session != null) {
      session.getAttribute(sessionAuthKey).asInstanceOf[Session]
    } else {
      halt(500)
    }
  }
}

