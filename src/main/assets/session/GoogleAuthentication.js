import Bacon from 'baconjs'

export function initAuthentication(properties) {
  return Bacon.fromBinder(sink => {
    gapi.load('auth2', () => {
      var proprs = {
        client_id: properties.googleAuthenticationClientId,
        //hosted_domain: properties.googleAuthenticationHostedDomain // todo: enable this
      }
      const auth2 = gapi.auth2.init(proprs)
      auth2.currentUser.listen(currentUser => {
        if (currentUser.isSignedIn()) {
          updateToLoggedInGoogle()
          const email = currentUser.getBasicProfile().getEmail()
          const token = currentUser.getAuthResponse().id_token
          const idpentityid = "google"
          sink({email, token, idpentityid})
        } else {
          updateToLoggedOutGoogle()
          sink({})
        }
      })
    })
  })
}

function updateToLoggedInGoogle() {
  updateAuthenticatioStatusImage("/hakuperusteet/img/button_google_signedin.png")
  const logoutLink = document.createElement("a")
  logoutLink.appendChild(document.createTextNode("Log out"))
  logoutLink.id = "logout"
  logoutLink.href = "#"
  logoutLink.onclick = logOut
  var authElem = document.getElementById('googleAuthentication')
  if (authElem != undefined) {
    authElem.appendChild(logoutLink)
  }
}

function updateToLoggedOutGoogle() {
  updateAuthenticatioStatusImage("/hakuperusteet/img/button_google_signin.png")
  const logoutLink = document.getElementById('logout')
  if (logoutLink != undefined) {
    document.getElementById('googleAuthentication').removeChild(logoutLink)
  }
}

function updateAuthenticatioStatusImage(src) {
  var signInButton = document.getElementById('googleAuthenticationStatus')
  if (signInButton != undefined) {
    signInButton.src = src
  }
}

function logOut() {
  gapi.auth2.getAuthInstance().signOut()
}

export function authenticationClick(_) {
  const auth = gapi.auth2.getAuthInstance()
  if (!auth.isSignedIn.get()) auth.signIn()
}