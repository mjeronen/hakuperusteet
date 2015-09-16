import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import {logOut} from './GoogleAuthentication'

export default class EmailSession extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const email = _.isUndefined(state.sessionData) ? "" : state.sessionData.email
    return <div id="emailAuthentication">
      <p>You are logged in as {email}.</p>
      <a id="logout" href="#" onClick={logOut(state, controller)}>Log out</a>
    </div>
  }
}
