import React from 'react'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'

import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"

export default class SelectedProgramInfo extends React.Component {
  render() {
    const state = this.props.state
    const tarjonta = tarjontaForHakukohdeOid(state, state.hakukohdeOid)
    const name = tarjonta.name
    const providerName = tarjonta.providerName
    const description = tarjonta.description
    const isLoading = !_.isEmpty(state.hakukohdeOid) && _.isEmpty(name) && _.isEmpty(description)

    return <div>
      { isLoading ?
        <AjaxLoader hide={false} />
        :
        <div>
          <h1>{providerName}</h1>
          <h1>{name}</h1>
          <p dangerouslySetInnerHTML={{__html: description}}/>
        </div>
      }
    </div>
  }
}