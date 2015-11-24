import React from 'react'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'

import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"
import * as translation from '../../assets-common/translations/translations.js'

export default class SelectedProgramInfo extends React.Component {
  render() {
    const state = this.props.state
    const tarjonta = tarjontaForHakukohdeOid(state, state.hakukohdeOid)
    const name = translation.resolveMap(tarjonta.name)
    const providerName = translation.resolveMap(tarjonta.providerName)
    const description = translation.resolveMap(tarjonta.description)
    const isLoading = !_.isEmpty(state.hakukohdeOid) && _.isEmpty(name) && _.isEmpty(description)

    return <div>
      { isLoading ?
        <AjaxLoader hide={false} />
        :
        <div>
          <h1>{providerName}</h1>
          <h1>{name}</h1>
          <div dangerouslySetInnerHTML={{__html: description}}/>
        </div>
      }
    </div>
  }
}