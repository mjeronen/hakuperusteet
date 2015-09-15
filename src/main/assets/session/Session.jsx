import React from 'react'
import _ from 'lodash'

import {showLoginInfo} from '../AppLogic.js'
import LoginInfo from './LoginInfo.jsx'
import GoogleAuthentication from './GoogleAuthentication.jsx'

export default class Session extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      { showLoginInfo(state) ? <LoginInfo state={state} /> : null}
      <GoogleAuthentication state={state} />
    </div>
  }
}
