import React from 'react'

export default class ProgramInfo extends React.Component {
  render() {
    const tarjonta = this.props.state.tarjonta
    const name = tarjonta.name
    const description = tarjonta.description
    console.log(description)
    return <section id="program-info">
      <h1>{name}</h1>
      <p dangerouslySetInnerHTML={{__html: description}}/>
    </section>
  }
}