import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../assets/util/HttpUtil.js'
import Dispatcher from '../assets/util/Dispatcher'
import {initChangeListeners} from '../assets/util/ChangeListeners'

const dispatcher = new Dispatcher()
const events = {
    updateField: 'updateField',
    submitForm: 'submitForm',
    fieldValidation: 'fieldValidation',
    logOut: 'logOut'
}

export function changeListeners() {
    return initChangeListeners(dispatcher, events)
}

export function initAppState(props) {
    const {propertiesUrl, usersUrl} = props
    const initialState = {}
    const fieldValidationS = dispatcher.stream(events.fieldValidation)
    const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
    const usersS = Bacon.fromPromise(HttpUtil.get(usersUrl))
    const S = Bacon.once("1.2.246.562.20.69046715533").toProperty()

    const stateP = Bacon.update(initialState,
        [propertiesS, usersS], onStateInit,
        [fieldValidationS], onFieldValidation)

    return stateP

    function onStateInit(state, properties, users) {
        return {...state, properties, users}
    }

    function onFieldValidation(state, {field, value}) {
        const newValidationErrors = parseNewValidationErrors(state, field, value)
        return {...state, ['validationErrors']: newValidationErrors}
    }

}