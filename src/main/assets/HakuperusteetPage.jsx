import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Header from './Header.jsx'
import Footer from './Footer.jsx'
import BaseEducation from './BaseEducation.jsx'

export default class HakuperusteetPage extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      <Header />
      <div>Hakuperusteet main page</div>
      <BaseEducation state={state} controller={controller} />
      <Footer />
    </div>
  }
}
