import React from 'react'
import _ from 'lodash'

import UserDataInput from './UserDataInput.jsx'
import UserBirthDateInput from './UserBirthDateInput.jsx'
import BaseEducation from './BaseEducation.jsx'

export default class UserDataForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <form id="userDataForm" onSubmit={controller.formSubmits}>
        <p>Please enter the following information.</p>
        <UserDataInput name="firstName" title="First name" state={state} controller={controller} />
        <UserDataInput name="lastName" title="Last name" state={state} controller={controller} />
        <UserBirthDateInput state={state} controller={controller} />
        <UserDataInput name="nationality" title="Nationality" state={state} controller={controller} />
        <BaseEducation state={state} controller={controller} />
        <input type="submit" name="submit" value="Submit" />
      </form>
  }
}
