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
    val matchedSessions: List[Session] = List(new TokenAuthStrategy(configuration, db, oppijanTunnistus), new GoogleOAuth2Strategy(configuration, db, googleVerifier)).flatMap(_.authenticate(request))
    if(matchedSessions.nonEmpty) {
      request.getSession.setAttribute(sessionAuthKey, matchedSessions.head)
    }
  }

  def logOut() = {
    val session = request.getSession(false)
    if (session != null) {
      session.invalidate()
    }
  }

  def user:Session = {
    val session = request.getSession(false)
    if (session != null) {
      session.getAttribute(sessionAuthKey).asInstanceOf[Session]
    } else {
      halt(500)
    }
  }
}

