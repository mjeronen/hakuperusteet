import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './util/HttpUtil'

export default class VetumaStart extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      Bacon.fromPromise(HttpUtil.get(state.properties.vetumaStartUrl)).onValue((result) => {
        form.action = result
        form.submit()
      })
    }
  }

  render() {
    const state = this.props.state
    return <form id="vetumaStart" onSubmit={this.onSubmitRedirect(state)} method="POST">
      <input type="submit" name="submitVetuma" value="Continue to payment" />
    </form>
  }
}
