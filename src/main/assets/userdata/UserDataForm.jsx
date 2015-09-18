import React from 'react'
import _ from 'lodash'

import UserDataInput from './UserDataInput.jsx'
import UserBirthDateInput from './UserBirthDateInput.jsx'
import UserSSNInput from './UserSSNInput.jsx'
import Gender from './Gender.jsx'
import Nationality from './Nationality.jsx'
import NativeLanguage from './NativeLanguage.jsx'
import EducationLevel from './EducationLevel.jsx'
import Countries from './Countries.jsx'
import CountryPaymentInfo from './CountryPaymentInfo.jsx'
import AjaxLoader from '../util/AjaxLoader.jsx'

import {validateUserDataForm} from './../util/FieldValidator.js'

export default class UserDataForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const disabled = (validateUserDataForm(state)) ? "" : "disabled"
    const languages = _.isUndefined(state.properties) ? [] : state.properties.languages
    const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
    return <form id="userDataForm" onSubmit={controller.formSubmits}>
        <p>Please enter the following information.</p>
        <UserDataInput name="firstName" title="First name" state={state} controller={controller} />
        <UserDataInput name="lastName" title="Last name" state={state} controller={controller} />
        <UserBirthDateInput state={state} controller={controller} />
        <UserSSNInput state={state} controller={controller} />
        <Gender state={state} controller={controller} />
        <NativeLanguage languages={languages} controller={controller} />
        <Nationality countries={countries} controller={controller} />
        <EducationLevel state={state} controller={controller} />
        <Countries countries={countries} controller={controller} lang="en" />
        <CountryPaymentInfo state={state} />
        <div className="userDataFormRow">
          <input type="submit" name="submit" value="Submit" disabled={disabled} />
          <AjaxLoader hide={true} />
          <span className="serverError invalid hide">Invalid userdata. Please check form values and try again.</span>
          <span className="serverError general hide">Unexpected server error. Please try again later.</span>
        </div>
      </form>
  }
}
