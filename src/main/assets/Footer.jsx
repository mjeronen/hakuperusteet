import React from 'react'

export default class Header extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return <section id="bottombar">
      <hr/>
      <div id="bottom-container">
        <img id="logo" src="img/logo.png"/>
      </div>
    </section>
  }
}