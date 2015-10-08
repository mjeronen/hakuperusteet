import React from 'react'
import _ from 'lodash'
import Bacon from 'baconjs'

import HttpUtil from '../assets/util/HttpUtil'
import UserDataInput from '../assets/userdata/UserDataInput.jsx'
import UserBirthDateInput from '../assets/userdata/UserBirthDateInput.jsx'
import UserSSNInput from '../assets/userdata/UserSSNInput.jsx'
import Gender from '../assets/userdata/Gender.jsx'
import Nationality from '../assets/userdata/Nationality.jsx'
import NativeLanguage from '../assets/userdata/NativeLanguage.jsx'
import AjaxLoader from './util/AjaxLoader.jsx'

import {validateUserDataForm} from '../assets/util/FieldValidator.js'
import {translation} from '../assets-common/translations/translations.js'

import EducationLevel from '../assets/education/EducationLevel.jsx'
import EducationCountry from '../assets/education/EducationCountry.jsx'

export default class HakuperusteetAdminForm extends React.Component {
    render() {
        const state = this.props.state
        const controller = this.props.controller
        const disabled = (validateUserDataForm(state)) ? "" : "disabled"
        const languages = _.isUndefined(state.properties) ? [] : state.properties.languages
        const countries = _.isUndefined(state.properties) ? [] : state.properties.countries
        const isUserSelected = state.id ? false : true
        console.log(isUserSelected + ", " + state.id)
        return <section id="hakuperusteet-admin-form">
            <form id="userDataForm" onSubmit={controller.formSubmits} hide={true}>
                <p>{translation("userdataform.info")}</p>
                <UserDataInput name="firstName" title={translation("title.first.name")} state={state} controller={controller} />
                <UserDataInput name="lastName" title={translation("title.last.name")} state={state} controller={controller} />
                <UserBirthDateInput state={state} controller={controller} />
                <UserSSNInput state={state} controller={controller} />
                <Gender state={state} controller={controller} />
                <NativeLanguage state={state} languages={languages} controller={controller} />
                <Nationality state={state} countries={countries} controller={controller} />
                <EducationLevel state={state} controller={controller} />
                <EducationCountry state={state} countries={countries} controller={controller} lang="en" />
                <div className="userDataFormRow">
                    <input type="submit" name="submit" value={translation("userdataform.submit")} disabled={disabled} />
                    <AjaxLoader hide={true} />
                    <span className="serverError invalid hide">{translation("errors.server.invalid.userdata")}</span>
                    <span className="serverError general hide">{translation("errors.server.unexpected")}</span>
                </div>
            </form>
        </section>
    }
}