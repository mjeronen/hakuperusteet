import React from 'react'
import _ from 'lodash'

export default class ProgramInfo extends React.Component {
  render() {
    const tarjonta = _.isUndefined(this.props.state.tarjonta) ? {name : "", description: ""} : this.props.state.tarjonta
    const name = tarjonta.name
    const description = tarjonta.description
    return <section id="program-info">
      <h1>{name}</h1>
      <p dangerouslySetInnerHTML={{__html: description}}/>
    </section>
  }
}