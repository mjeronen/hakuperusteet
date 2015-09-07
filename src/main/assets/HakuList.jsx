import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './util/HttpUtil'

export default class HakuList extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      Bacon.fromPromise(HttpUtil.get(state.properties.formRedirectUrl)).onValue((result) => {
        window.location = result.url
      })
    }
  }

  render() {
    const state = this.props.state
    return <form id="redirectToForm" onSubmit={this.onSubmitRedirect(state)} method="GET">
        <input type="submit" name="redirectToForm" value="Proceed to application form" />
      </form>
  }
}
