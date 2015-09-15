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
          const email = currentUser.getBasicProfile().getEmail()
          const token = currentUser.getAuthResponse().id_token
          const idpentityid = "google"
          sink({email, token, idpentityid})
        } else {
          sink({})
        }
      })
    })
  })
}

function logOut() {
  gapi.auth2.getAuthInstance().signOut()
}

export function authenticationClick(_) {
  const auth = gapi.auth2.getAuthInstance()
  if (!auth.isSignedIn.get()) auth.signIn()
}