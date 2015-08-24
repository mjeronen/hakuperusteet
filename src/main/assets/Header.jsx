import React from 'react'

export default class Header extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return <section id="topbar">
      <div id="top-container">
        <h1 id="topic">
          Opetushallitus - KSHJ2
        </h1>
      </div>
      <hr/>
    </section>
  }
}