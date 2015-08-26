export function initAuthentication(dispatcher, events) {
  return (properties) => {
    gapi.load('auth2', () => {
      const auth2 = gapi.auth2.init({ client_id: properties.googleAuthenticationClientId })
      auth2.currentUser.listen((currentUser) => {
        const email = currentUser.getBasicProfile().getEmail()
        if (currentUser.isSignedIn()) {
          const token = currentUser.getAuthResponse().id_token
          dispatcher.push(events.signIn, { email: email, token: token } )
        } else {
          dispatcher.push(events.signOut, { email: email } )
        }
      } )
    })
  }
}

export function googleAuthenticationRenderFailure(x) {
  console.log("Google auth render error")
  console.log(x)
}

