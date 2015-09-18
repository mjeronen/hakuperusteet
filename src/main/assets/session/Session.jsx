import React from 'react'
import _ from 'lodash'

import {sessionInit, showLoginInfo, hasGoogleSession, hasEmailSession} from '../AppLogic.js'
import LoginInfo from './LoginInfo.jsx'
import GoogleLogIn from './GoogleLogIn.jsx'
import GoogleSession from './GoogleSession.jsx'
import EmailLogIn from './EmailLogIn.jsx'
import EmailSession from './EmailSession.jsx'

export default class Session extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <section id="session">
      { !sessionInit(state) ? <img className="ajax-loader" src="/hakuperusteet/img/ajax-loader.gif" /> : null}
      { showLoginInfo(state) ? <LoginInfo state={state} /> : null}
      { showLoginInfo(state) ? <GoogleLogIn state={state} controller={controller} /> : null}
      { showLoginInfo(state) ? <EmailLogIn state={state} controller={controller} /> : null}
      { hasGoogleSession(state) ? <GoogleSession state={state} controller={controller} /> : null}
      { hasEmailSession(state) ? <EmailSession state={state} controller={controller} /> : null}
    </section>
  }
}
