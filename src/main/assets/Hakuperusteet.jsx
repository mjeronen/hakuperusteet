import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Header from './Header.jsx'
import Footer from './Footer.jsx'

export default class HakuperusteetApp extends React.Component {

  render() {
    return <div>
        <Header />
        <div>Hakuperusteet main page</div>
        <Footer />
      </div>
  }

}
React.render(React.createElement(HakuperusteetApp, {}), document.getElementById('app'))