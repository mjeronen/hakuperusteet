import React from 'react'
import _ from 'lodash'
import Bacon from 'baconjs'

import {createSelectOptions} from '../assets/util/HtmlUtils.js'
import HttpUtil from '../assets/util/HttpUtil'
import AjaxLoader from './util/AjaxLoader.jsx'

import {validateUserDataForm, requiredField, invalidField} from '../assets/util/FieldValidator.js'
import {translation} from '../assets-common/translations/translations.js'

import EducationLevel from '../assets/education/EducationLevel.jsx'
import EducationCountry from '../assets/education/EducationCountry.jsx'
import EducationErrors from '../assets/education/EducationErrors.jsx'
import {tarjontaForHakukohdeOid} from "../assets/util/TarjontaUtil.js"

import UserDataForm from './userdata/UserDataForm.jsx'
import UserDataErrors from '../assets/userdata/UserDataErrors.jsx'

export default class HakuperusteetAdminForm extends React.Component {
    render() {
        const state = this.props.state
        const controller = this.props.controller
        const disabled = (validateUserDataForm(state)) ? "" : "disabled"
        const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
        const countriesResult = createSelectOptions(countries)
        const allBaseEducations = (_.isEmpty(state.properties) || _.isEmpty(state.properties.baseEducation)) ? [] : state.properties.baseEducation
        const isUserSelected = state.id ? true : false
        const applicationObjects = _.isEmpty(state.applicationObjects) ? [] : state.applicationObjects
        const changes = this.props.controller.pushEducationFormChanges

        if(isUserSelected) {
        return <section className="main-content oppija">
            <UserDataForm state={state} controller={controller} />
            {applicationObjects.map((ao, i) => {
                const tarjonta = tarjontaForHakukohdeOid(state, ao.hakukohdeOid)
                const baseEducationsForCurrent = tarjonta.baseEducations
                const baseEducationOptions = allBaseEducations.filter(function(b) { return _.contains(baseEducationsForCurrent, b.id) })
                const levelResult = createSelectOptions(baseEducationOptions)
                const formId = "educationForm_" + ao.hakukohdeOid
                const name = tarjonta.name

                return <form id={formId} onSubmit={controller.formSubmits}>
                    <p><strong>{name}.</strong></p>
                    <br/>
                    <div className="userDataFormRow">
                        <label htmlFor="educationLevel">{translation("title.education.level") + " *"}</label>
                        <select id="educationLevel" onChange={changes.bind(this, ao)} onBlur={changes.bind(this, ao)} value={ao.educationLevel}>
                             {levelResult}
                        </select>
                    </div>
                    <div className="userDataFormRow">
                        <label htmlFor="educationCountry">{translation("title.education.country") + " *"}</label>
                        <select id="educationCountry" onChange={changes.bind(this, ao)} onBlur={changes.bind(this, ao)} value={ao.educationCountry}>
                            {countriesResult}
                        </select>
                    </div>
                    <div className="userDataFormRow">
                        <input type="submit" name="submit" value={translation("educationForm.submit")} disabled={disabled} />
                        <AjaxLoader hide={true} />
                        <span className="serverError general hide">{translation("errors.server.unexpected")}</span>
                    </div>
                    <div className="userDataFormRow">
                      { requiredField(ao, "educationLevel") ? <span className="error">{translation("educationForm.errors.requiredEducationLevel")}</span> : null}
                      { requiredField(ao, "educationCountry") ? <span className="error">{translation("educationForm.errors.requiredEducationCountry")}</span> : null}
                    </div>
                </form>
            })}
        </section>
        } else {
            return <section/>;
        }
    }

    submitEducation(ao, submit, e) {
        console.log(submit)
        e.preventDefault()
    }

}