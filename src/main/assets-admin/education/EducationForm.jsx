import React from 'react'
import _ from 'lodash'

import {tarjontaForHakukohdeOid} from "../../assets/util/TarjontaUtil.js"

import {createSelectOptions} from '../../assets/util/HtmlUtils.js'
import HttpUtil from '../../assets/util/HttpUtil'
import AjaxLoader from '../util/AjaxLoader.jsx'

import {validateUserDataForm, requiredField, invalidField} from '../../assets/util/FieldValidator.js'
import {validateApplicationObject} from '../util/ApplicationObjectValidator.js'
import {translation} from '../../assets-common/translations/translations.js'


export default class EducationForm extends React.Component {
  constructor(props) {
    super()
    this.changes = props.controller.pushEducationFormChanges
  }

  componentDidMount() {
    this.changes(this.props.applicationObject)
  }

  render() {
    const state = this.props.state
    const controller = this.props.controller
    const ao = this.props.applicationObject
    const allBaseEducations = (_.isEmpty(state.properties) || _.isEmpty(state.properties.baseEducation)) ? [] : state.properties.baseEducation
    const tarjonta = tarjontaForHakukohdeOid(state, ao.hakukohdeOid)
    const baseEducationsForCurrent = tarjonta.baseEducations
    const baseEducationOptions = allBaseEducations.filter(function(b) { return _.contains(baseEducationsForCurrent, b.id) })
    const levelResult = createSelectOptions(baseEducationOptions)
    const formId = "educationForm_" + ao.hakukohdeOid
    const name = tarjonta.name
    const disabled = (validateApplicationObject(ao) && !requiredField(ao, "noChanges")) ? "" : "disabled"

    const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
    const countriesResult = createSelectOptions(countries)


    const errors = requiredField(ao, "noChanges") ? <div className="userDataFormRow">
      <span className="error">Lomakkeella ei ole muuttuneita tietoja</span>
    </div> : <div className="userDataFormRow">
                      { requiredField(ao, "educationLevel") ? <span className="error">{translation("educationForm.errors.requiredEducationLevel")}</span> : null}
                      { requiredField(ao, "educationCountry") ? <span className="error">{translation("educationForm.errors.requiredEducationCountry")}</span> : null}
    </div>

    return <form id={formId} onSubmit={controller.formSubmits}>
      <p><strong>{name}.</strong></p>
      <br/>
      <div className="userDataFormRow">
        <label htmlFor="educationLevel">{translation("title.education.level") + " *"}</label>
        <select id="educationLevel" onChange={this.changes.bind(this, ao)} onBlur={this.changes.bind(this, ao)} value={ao.educationLevel}>
                             {levelResult}
        </select>
      </div>
      <div className="userDataFormRow">
        <label htmlFor="educationCountry">{translation("title.education.country") + " *"}</label>
        <select id="educationCountry" onChange={this.changes.bind(this, ao)} onBlur={this.changes.bind(this, ao)} value={ao.educationCountry}>
                            {countriesResult}
        </select>
      </div>
      <div className="userDataFormRow">
        <input type="submit" name="submit" value={translation("educationForm.submit")} disabled={disabled} />
        <AjaxLoader hide={true} />
        <span className="serverError general hide">{translation("errors.server.unexpected")}</span>
      </div>
      {errors}
    </form>
  }
}
