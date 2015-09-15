import React from 'react'
import _ from 'lodash'

import {authenticationClick} from './GoogleAuthentication'

export default class GoogleAuthentication extends React.Component {
  render() {
    return <div id="googleAuthentication">
        <img id="googleAuthenticationStatus" src="/hakuperusteet/img/ajax-loader.gif" onClick={authenticationClick} />
      </div>
  }
}
