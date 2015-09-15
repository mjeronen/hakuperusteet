import React from 'react'
import _ from 'lodash'

import {authenticationClick} from './GoogleAuthentication'

export default class GoogleLogIn extends React.Component {
  render() {
    return <div id="googleAuthentication">
      <img id="googleAuthenticationStatus" src="/hakuperusteet/img/button_google_signin.png" onClick={authenticationClick} />
    </div>
  }
}
