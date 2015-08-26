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

export default class GoogleAuthentication {

  static onSuccess(x) {
    console.log(x)
  }

  static onFailure() {
    console.log("failure")
  }
}

