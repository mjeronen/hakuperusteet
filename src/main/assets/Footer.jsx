import React from 'react'

export default class Header extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return <section id="topbar">
      <hr/>
      <div id="top-container">
        <img id="logo" src="img/logo.png"/>
      </div>
    </section>
  }
}