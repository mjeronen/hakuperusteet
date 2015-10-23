import React from 'react'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'
import EmptyProgramInfo from './EmptyProgramInfo.jsx'
import SelectedProgramInfo from './SelectedProgramInfo.jsx'

import {translation} from '../../assets-common/translations/translations.js'
import {fatalError, serverError, maksumuuriInUseWithSelectedHakukohdeOid, hakuForSelectedHakukohdeOidIsOpen} from '../AppLogic.js'
import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"

export default class ProgramInfo extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <section id="program-info">
      { !fatalError(state) && _.isEmpty(state.hakukohdeOid) ? <EmptyProgramInfo state={state} controller={controller} /> : null}
      { !fatalError(state) && !_.isEmpty(state.hakukohdeOid) ? <SelectedProgramInfo state={state} controller={controller} /> : null}
      { serverError(state) ? <p className="serverError">{translation("errors.server.pageload")}</p> : null}
      { !maksumuuriInUseWithSelectedHakukohdeOid(state) ? <p className="serverError">{translation("errors.tarjonta.invalid.hakukohde")}</p> : null}
      { !hakuForSelectedHakukohdeOidIsOpen(state) ? <p className="serverError">{translation("errors.tarjonta.invalid.hakuaika")}</p> : null}
    </section>
  }
}