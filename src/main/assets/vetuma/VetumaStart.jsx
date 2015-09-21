import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../util/HttpUtil'
import AjaxLoader from '../util/AjaxLoader.jsx'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusyAndShowError} from '../util/HtmlUtils.js'
import {translation} from '../translations/translations.js'

export default class VetumaStart extends React.Component {
  onSubmitRedirect(state) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      disableSubmitAndShowBusy(form)
      const promise = Bacon.fromPromise(HttpUtil.get(state.properties.vetumaStartUrl))
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
      <p>{translation("vetuma.start.info")}</p>
      <form id="vetumaStart" onSubmit={this.onSubmitRedirect(state)} method="POST">
        <input type="submit" name="submitVetuma" value={translation("vetuma.start.submit")} />
        <AjaxLoader hide={true} />
        <span className="serverError hide">{translation("errors.server.unexpected")}</span>
      </form>
    </div>
  }
}
