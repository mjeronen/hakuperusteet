import React from 'react'
import _ from 'lodash'

import EducationLevel from './EducationLevel.jsx'
import EducationCountry from './EducationCountry.jsx'
import CountryPaymentInfo from './CountryPaymentInfo.jsx'
import AjaxLoader from '../util/AjaxLoader.jsx'
import EducationErrors from './EducationErrors.jsx'

import {validateEducationForm} from './../util/FieldValidator.js'
import {translation} from '../../assets-common/translations/translations.js'
import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"

export default class EducationForm extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const tarjonta = tarjontaForHakukohdeOid(state, state.hakukohdeOid)
    const name = getTarjontaNameOrFallback(tarjonta.name, resolveLang())
    const disabled = (validateEducationForm(state)) ? "" : "disabled"
    const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
    return <form id="educationForm" onSubmit={controller.formSubmits}>
      <p>
        {translation("educationForm.info")} <strong>{name}.</strong>
      </p>
      <p>
        {translation("educationForm.payment")}
      </p>
      <EducationLevel state={state} controller={controller} />
      <EducationCountry state={state} countries={countries} controller={controller} lang="en" />
      <CountryPaymentInfo state={state} />
      <div className="userDataFormRow">
        <input type="submit" name="submit" value={translation("educationForm.submit")} disabled={disabled} />
        <AjaxLoader hide={true} />
        <span className="serverError general hide">{translation("errors.server.unexpected")}</span>
      </div>
      <EducationErrors state={state} controller={controller} />
    </form>
  }
}
