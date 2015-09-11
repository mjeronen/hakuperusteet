import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './util/HttpUtil'
import {disableSubmitAndShowBusy} from './util/HtmlUtils.js'

export default class HakuList extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      disableSubmitAndShowBusy(form)
      Bacon.fromPromise(HttpUtil.get(state.properties.formRedirectUrl)).onValue((result) => {
        window.location = result.url
      })
    }
  }

  render() {
    const state = this.props.state
    return <div className="hakuList">
        <p>Continue to application form with following link.</p>
        <form id="redirectToForm" onSubmit={this.onSubmitRedirect(state)} method="GET">
          <input type="submit" name="redirectToForm" value="Proceed to application form" />
          <img className="ajax-loader hide" src="/hakuperusteet/img/ajax-loader.gif" />
        </form>
      </div>
  }
}
