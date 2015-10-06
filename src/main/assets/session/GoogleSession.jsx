import React from 'react'
import _ from 'lodash'

import {logOut} from './GoogleAuthentication'
import {translation} from '../../assets-common/translations/translations.js'

export default class GoogleSession extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller

    return <div className="googleAuthentication session">
      <img src="/hakuperusteet/img/button_google_signedin.png" />
      <a id="logout" href="#" onClick={logOut(state, controller)}>{translation("logout")}</a>
    </div>
  }
}
