import React from 'react'
import _ from 'lodash'

import {tarjontaForHakukohdeOid} from "./util/TarjontaUtil.js"

export default class ProgramInfo extends React.Component {
  render() {
    const tarjonta = tarjontaForHakukohdeOid(this.props.state, this.props.state.hakukohdeOid)
    const name = tarjonta.name
    const description = tarjonta.description
    return <section id="program-info">
      <h1>{name}</h1>
      <p dangerouslySetInnerHTML={{__html: description}}/>
    </section>
  }
}