import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import BaseEducation from './BaseEducation.jsx'

export default class UserDataForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <form>
        <BaseEducation state={state} controller={controller} />
        <input type="submit" name="submit" value="Submit" />
      </form>
  }
}
