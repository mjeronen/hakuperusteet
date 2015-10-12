import React from 'react'
import _ from 'lodash'

import AjaxLoader from '../util/AjaxLoader.jsx'
import EmptyProgramInfo from './EmptyProgramInfo.jsx'
import SelectedProgramInfo from './SelectedProgramInfo.jsx'

import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"

export default class ProgramInfo extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <section id="program-info">
      { _.isEmpty(state.hakukohdeOid) ? <EmptyProgramInfo state={state} controller={controller} /> : null}
      { !_.isEmpty(state.hakukohdeOid) ? <SelectedProgramInfo state={state} controller={controller} /> : null}
    </section>
  }
}