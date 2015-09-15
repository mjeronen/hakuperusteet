import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

export default class EmailSession extends React.Component {
  logOut(state, controller) {
    return (e) => {
      e.preventDefault()
      const promise = Bacon.fromPromise(HttpUtil.get(state.properties.logOutUrl))
      promise.onValue((result) => {
        controller.logOut()
      })
      promise.onError((_) => { console.log("logout error") })
    }
  }

  render() {
    const state = this.props.state
    const controller = this.props.controller
    const email = _.isUndefined(state.session) ? "" : state.session.email
    return <div id="emailAuthentication">
      <p>You are logged in as {email}.</p>
      <a id="logout" href="#" onClick={this.logOut(state, controller)}>Log out</a>
    </div>
  }
}
