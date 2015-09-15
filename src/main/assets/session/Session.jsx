import React from 'react'
import _ from 'lodash'

import {showLoginInfo} from '../AppLogic.js'
import LoginInfo from './LoginInfo.jsx'
import GoogleLogIn from './GoogleLogIn.jsx'
import GoogleLogOut from './GoogleLogOut.jsx'
import EmailLogIn from './EmailLogIn.jsx'
import EmailLogOut from './EmailLogOut.jsx'

export default class Session extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      { showLoginInfo(state) ? <LoginInfo state={state} /> : null}
      { showLoginInfo(state) ? <GoogleLogIn state={state} controller={controller} /> : null}
      { showLoginInfo(state) ? <EmailLogIn state={state} controller={controller} /> : null}
    </div>
  }
}
