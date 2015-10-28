import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'
import {fetchUrlParamsAndRedirectPost} from '../util/FormUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class VetumaStart extends React.Component {

  render() {
    const state = this.props.state
    const vetumaStartUrl = state.properties.vetumaStartUrl + (_.isEmpty(state.hakukohdeOid) ?  "" : "/" + state.hakukohdeOid)
    return <div className="vetumaStart">
      <p dangerouslySetInnerHTML={{__html: translation("vetuma.start.info")}}/>
      <form id="vetumaStart" onSubmit={fetchUrlParamsAndRedirectPost( vetumaStartUrl)} method="POST">
        <input type="submit" name="submitVetuma" value={translation("vetuma.start.submit")} />
        <AjaxLoader hide={true} />
        <span className="serverError hide">{translation("errors.server.unexpected")}</span>
      </form>
    </div>
  }
}
