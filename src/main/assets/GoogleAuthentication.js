export default class GoogleAuthentication {
  constructor(dispatcher) {
    this.dispatcher = dispatcher
  }

  initialize(properties) {
    gapi.load('auth2', GoogleAuthentication.initiGoogleAuthentication(properties))
  }

  static initiGoogleAuthentication(properties) {
    return () => {
      const auth2 = gapi.auth2.init({ client_id: properties.googleAuthenticationClientId })
      auth2.isSignedIn.listen(GoogleAuthentication.signinChanged)
    }
  }

  static onSuccess(x) {
    console.log(x)
  }

  static onFailure() {
    console.log("failure")
  }

  static signinChanged(val) {
    console.log(val)
  }
}

