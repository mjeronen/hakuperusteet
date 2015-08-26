import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import UserDataInput from './UserDataInput.jsx'
import BaseEducation from './BaseEducation.jsx'

export default class UserDataForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <form>
        <p>Please enter the following information.</p>
        <UserDataInput name="firstName" title="First name" controller={controller} />
        <UserDataInput name="lastName" title="Last name" controller={controller} />

        <UserDataInput name="nationality" title="Nationality" controller={controller} />
        <BaseEducation state={state} controller={controller} />
        <input type="submit" name="submit" value="Submit" />
      </form>
  }
}
