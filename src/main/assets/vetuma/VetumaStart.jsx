import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'
import {fetchUrlParamsAndRedirectPost} from '../util/FormUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class VetumaStart extends React.Component {

  render() {
    const state = this.props.state
    return <div className="vetumaStart">
      <p>{translation("vetuma.start.info")}</p>
      <form id="vetumaStart" onSubmit={fetchUrlParamsAndRedirectPost(state.properties.vetumaStartUrl)} method="POST">
        <input type="submit" name="submitVetuma" value={translation("vetuma.start.submit")} />
        <AjaxLoader hide={true} />
        <span className="serverError hide">{translation("errors.server.unexpected")}</span>
      </form>
    </div>
  }
}
