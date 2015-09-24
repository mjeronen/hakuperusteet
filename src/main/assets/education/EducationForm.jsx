import React from 'react'
import _ from 'lodash'

import EducationLevel from './EducationLevel.jsx'
import Countries from './Countries.jsx'
import CountryPaymentInfo from './CountryPaymentInfo.jsx'
import AjaxLoader from '../util/AjaxLoader.jsx'

import {validateEducationForm} from './../util/FieldValidator.js'
import {translation} from '../translations/translations.js'

export default class EducationForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const disabled = (validateEducationForm(state)) ? "" : "disabled"
    const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
    return <form id="educationForm" onSubmit={controller.formSubmits}>
      <p>{translation("educationForm.info")}</p>
      <EducationLevel state={state} controller={controller} />
      <Countries countries={countries} controller={controller} lang="en" />
      <CountryPaymentInfo state={state} />
      <div className="userDataFormRow">
        <input type="submit" name="submit" value={translation("educationForm.submit")} disabled={disabled} />
        <AjaxLoader hide={true} />
        <span className="serverError general hide">{translation("errors.server.unexpected")}</span>
      </div>
    </form>
  }
}
