import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import {logOut} from './GoogleAuthentication'
import {translation} from '../../assets-common/translations/translations.js'

export default class EmailSession extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const email = _.isUndefined(state.sessionData) ? "" : state.sessionData.session.email
    return <div className="emailAuthentication session">
      <p>{translation("login.logged.in.as")} <span className="loggedInAs">{email}</span>.</p>
      <a id="logout" href="#" onClick={logOut(state, controller)}>{translation("logout")}</a>
    </div>
  }
}
