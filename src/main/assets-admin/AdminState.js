import Bacon from 'baconjs'
import _ from 'lodash'

import {enableSubmitAndHideBusy} from '../assets/util/HtmlUtils.js'
import HttpUtil from '../assets/util/HttpUtil.js'
import Dispatcher from '../assets/util/Dispatcher'
import {initChangeListeners} from '../assets/util/ChangeListeners'
import {parseNewValidationErrors} from '../assets/util/FieldValidator.js'

const dispatcher = new Dispatcher()
const events = {
    route: 'route',
    updateField: 'updateField',
    submitForm: 'submitForm',
    fieldValidation: 'fieldValidation',
    logOut: 'logOut'
}

export function changeListeners() {
    return initChangeListeners(dispatcher, events)
}

export function initAppState(props) {
    const {propertiesUrl, usersUrl, userUpdateUrl} = props
    const initialState = {}
    const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
    const usersS = Bacon.fromPromise(HttpUtil.get(usersUrl))
    const serverUpdatesBus = new Bacon.Bus()

    var personOidInUrl = function(url) {
        return url.match(new RegExp("oppija/(.*)")) ? match[1] : null
    }
    const updateRouteS = Bacon.mergeAll(dispatcher.stream(events.route),Bacon.once(document.location.pathname).toProperty())
        .map(personOidInUrl)
        .skipDuplicates(_.isEqual)
        .flatMap(function(uniquePersonOid) {
            return Bacon.fromPromise(HttpUtil.get(`/hakuperusteetadmin/api/v1/admin/${uniquePersonOid}`))
        })

    const updateFieldS = dispatcher.stream(events.updateField).merge(serverUpdatesBus)
    const formSubmittedS = dispatcher.stream(events.submitForm)
    const fieldValidationS = dispatcher.stream(events.fieldValidation)

    const stateP = Bacon.update(initialState,
        [propertiesS, usersS], onStateInit,
        [updateRouteS],onUpdateUser,
        [updateFieldS], onUpdateField,
        [fieldValidationS], onFieldValidation)

    serverUpdatesBus.plug(stateP.sampledBy(formSubmittedS, (state, form) => ({state, form})).flatMapLatest(({state}) => {
            const userData = {
                id: state.id,
                email: state.email,
                firstName: state.firstName,
                lastName: state.lastName,
                birthDate: state.birthDate,
                personOid: state.personOid,
                personId: state.personId,
                gender: state.gender,
                nativeLanguage: state.nativeLanguage,
                nationality: state.nationality,
                idpentityid: state.idpentityid
            }
            const promise = Bacon.fromPromise(HttpUtil.post(userUpdateUrl, userData))
            promise.onError((error) => {
                const form = document.getElementById('userDataForm')
                enableSubmitAndHideBusy(form)
                if (error.status == 409) {
                    form.querySelector("span.invalid").classList.remove("hide")
                } else {
                    form.querySelector("span.general").classList.remove("hide")
                }
            })
            return promise
        }).map((result) => {
        const form = document.getElementById('userDataForm')
        enableSubmitAndHideBusy(form)
        return result
    }))

    return stateP

    function onUpdateField(state, {field, value}) {
        return {...state, [field]: value}
    }
    function onUpdateUser(state, user) {
        return {...state, ...user.user, ['applicationObjects']: user.applicationObject}
    }
    function onStateInit(state, properties, users) {
        return {...state, properties, users}
    }
    function onFieldValidation(state, {field, value}) {
        const newValidationErrors = parseNewValidationErrors(state, field, value)
        return {...state, ['validationErrors']: newValidationErrors}
    }

}