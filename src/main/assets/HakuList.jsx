import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './util/HttpUtil'
import AjaxLoader from './util/AjaxLoader.jsx'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusyAndShowError} from './util/HtmlUtils.js'

export default class HakuList extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      disableSubmitAndShowBusy(form)
      const promise = Bacon.fromPromise(HttpUtil.get(state.properties.formRedirectUrl))
      promise.onValue((result) => { window.location = result.url })
      promise.onError((_) => { enableSubmitAndHideBusyAndShowError(form) })
    }
  }

  render() {
    const state = this.props.state
    return <div className="hakuList">
        <p>Continue to application form with following link.</p>
        <form id="redirectToForm" onSubmit={this.onSubmitRedirect(state)} method="GET">
          <input type="submit" name="redirectToForm" value="Proceed to application form" />
          <AjaxLoader hide={true} />
          <span className="serverError hide">Unexpected server error. Please try again later.</span>
        </form>
      </div>
  }
}
