import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

export default class HakuperusteetApp extends React.Component {

  render() {
    return <div>Hakuperusteet main page</div>
  }

}
React.render(React.createElement(HakuperusteetApp, {}), document.getElementById('app'))