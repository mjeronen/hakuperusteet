import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../util/HttpUtil'

export default class VetumaStart extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      form.querySelector("input[type=submit]").setAttribute("disabled", "disabled")
      form.querySelector(".ajax-loader").className = "ajax-loader"
      Bacon.fromPromise(HttpUtil.get(state.properties.vetumaStartUrl)).onValue((result) => {
        form.action = result
        form.submit()
      })
    }
  }

  render() {
    const state = this.props.state
    return <div className="vetumaStart">
      <p>You are required to pay application fee of 100€ before continuing to the application form.</p>
      <form id="vetumaStart" onSubmit={this.onSubmitRedirect(state)} method="POST">
        <input type="submit" name="submitVetuma" value="Continue to payment" />
        <img className="ajax-loader hide" src="/hakuperusteet/img/ajax-loader.gif" />
      </form>
    </div>
  }
}
