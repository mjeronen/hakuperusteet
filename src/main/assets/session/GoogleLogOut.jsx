import React from 'react'
import _ from 'lodash'

import {logOut} from './GoogleAuthentication'

export default class GoogleLogOut extends React.Component {
  render() {
    return <div id="googleAuthentication">
      <img id="googleAuthenticationStatus" src="/hakuperusteet/img/button_google_signedin.png" />
      <a id="logout" href="#" onClick="logOut">Log out</a>
    </div>
  }
}
