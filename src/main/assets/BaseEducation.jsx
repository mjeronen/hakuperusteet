import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Countries from './Countries.jsx'

export default class BaseEducation extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return <div>
      <p>Please select your base education country.</p>
      <Countries countries={this.props.countries} />
    </div>
  }
}
