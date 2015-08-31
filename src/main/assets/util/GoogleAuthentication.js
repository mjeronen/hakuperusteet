import Bacon from 'baconjs'

export function initAuthentication(properties) {
  return Bacon.fromBinder(sink => {
    gapi.load('auth2', () => {
      const auth2 = gapi.auth2.init({client_id: properties.googleAuthenticationClientId, hosted_domain:properties.googleAuthenticationHostedDomain})
      auth2.currentUser.listen(currentUser => {
        if (currentUser.isSignedIn()) {
          const email = currentUser.getBasicProfile().getEmail()
          const token = currentUser.getAuthResponse().id_token
          sink({email, token})
        } else {
          sink({})
        }
      })
    })
  })
}
export function googleAuthenticationRenderFailure(x) {
  console.log("Google auth render error")
  console.log(x)
}

