import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../util/HttpUtil'
import AjaxLoader from '../util/AjaxLoader.jsx'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusyAndShowError} from '../util/HtmlUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class HakuList extends React.Component {
  onSubmitRedirect(state, hakukohdeOid) {
    return (e) => {
      e.preventDefault()
      const form = e.target
      disableSubmitAndShowBusy(form)
      const promise = Bacon.fromPromise(HttpUtil.get(state.properties.formRedirectUrl + "?hakukohdeOid=" + hakukohdeOid))
      promise.onValue((result) => { window.location = result.url })
      promise.onError((_) => { enableSubmitAndHideBusyAndShowError(form) })
    }
  }

  render() {
    const state = this.props.state
    return <div className="hakuList">
      <p>{translation("hakulist.info")}</p>

      {[...state.sessionData.applicationObject].map((x, i) =>
        <div key={i}>
          <p>{x.hakukohdeOid}</p>
          <form className="redirectToForm" onSubmit={this.onSubmitRedirect(state, x.hakukohdeOid)} method="GET">
            <input type="submit" name="redirectToForm" value={translation("hakulist.submit")} />
            <AjaxLoader hide={true} />
            <span className="serverError hide">{translation("errors.server.unexpected")}</span>
          </form>
        </div>
      )}

      </div>
  }
}
