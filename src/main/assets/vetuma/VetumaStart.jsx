import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../util/HttpUtil'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusyAndShowError} from '../util/HtmlUtils.js'

export default class VetumaStart extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      disableSubmitAndShowBusy(form)
      var promise = Bacon.fromPromise(HttpUtil.get(state.properties.vetumaStartUrl))
      promise.onValue((result) => {
        form.action = result
        form.submit()
      })
      promise.onError((_) => { enableSubmitAndHideBusyAndShowError(form) })
    }
  }

  render() {
    const state = this.props.state
    return <div className="vetumaStart">
      <p>You are required to pay application fee of 100€ before continuing to the application form.</p>
      <form id="vetumaStart" onSubmit={this.onSubmitRedirect(state)} method="POST">
        <input type="submit" name="submitVetuma" value="Continue to payment" />
        <img className="ajax-loader hide" src="/hakuperusteet/img/ajax-loader.gif" />
        <span className="serverError hide">Unexpected server error. Please try again later.</span>
      </form>
    </div>
  }
}
