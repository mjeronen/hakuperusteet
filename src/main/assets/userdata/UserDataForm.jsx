import React from 'react'
import _ from 'lodash'

import UserDataInput from './UserDataInput.jsx'
import UserBirthDateInput from './UserBirthDateInput.jsx'
import UserSSNInput from './UserSSNInput.jsx'
import Gender from './Gender.jsx'
import EducationLevel from './EducationLevel.jsx'
import Countries from './Countries.jsx'
import CountryPaymentInfo from './CountryPaymentInfo.jsx'

export default class UserDataForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <form id="userDataForm" onSubmit={controller.formSubmits}>
        <p>Please enter the following information.</p>
        <UserDataInput name="firstName" title="First name" state={state} controller={controller} />
        <UserDataInput name="lastName" title="Last name" state={state} controller={controller} />
        <UserBirthDateInput state={state} controller={controller} />
        <UserSSNInput state={state} controller={controller} />
        <Gender state={state} controller={controller} />
        <UserDataInput name="nationality" title="Nationality" state={state} controller={controller} />
        <EducationLevel educationLevel={state.educationLevel} controller={controller} />
        <Countries countries={state.countries} controller={controller} lang="en" />
        <CountryPaymentInfo state={state} />
        <div className="userDataFormRow">
          <input type="submit" name="submit" value="Submit" />
        </div>
      </form>
  }
}
