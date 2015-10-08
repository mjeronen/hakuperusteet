import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../assets/util/HttpUtil.js'
import Dispatcher from '../assets/util/Dispatcher'
import {initChangeListeners} from '../assets/util/ChangeListeners'

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
    const {propertiesUrl, usersUrl} = props
    const initialState = {}
    const fieldValidationS = dispatcher.stream(events.fieldValidation)
    const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
    const usersS = Bacon.fromPromise(HttpUtil.get(usersUrl))
    //const serverUpdatesBus = new Bacon.Bus()

    var personOidInUrl = function(url) {
        var match = url.match(new RegExp("oppija/(.*)"));
        if(match) {
            return match[1];
        } else {
            return null;
        }
    }
    const updateRouteS = Bacon.mergeAll(dispatcher.stream(events.route),Bacon.once(document.location.pathname).toProperty())
        .map(personOidInUrl)
        //.filter(_.isNotEmpty)
        .skipDuplicates(_.isEqual)
        .flatMap(function(uniquePersonOid) {
            console.log('jei ' + uniquePersonOid)
            return Bacon.fromPromise(HttpUtil.get(`/hakuperusteetadmin/api/v1/admin/${uniquePersonOid}`));
        }).log()

    const updateFieldS = dispatcher.stream(events.updateField)//.merge(serverUpdatesBus)
    const stateP = Bacon.update(initialState,
        [propertiesS, usersS], onStateInit,
        [updateRouteS],onUpdateUser,
        [updateFieldS], onUpdateField)

    //serverUpdatesBus.plug(updateRouteS)

    return stateP

    function onUpdateField(state, {field, value}) {
        return {...state, [field]: value}
    }
    function onUpdateUser(state, user) {
        return {...state, ...user}
    }
    function onStateInit(state, properties, users) {
        return {...state, properties, users}
    }
    function onUpdateRoute(state, selectedUser) {
        return {...state, selectedUser}
    }
    function onFieldValidation(state, {field, value}) {
        const newValidationErrors = parseNewValidationErrors(state, field, value)
        return {...state, ['validationErrors']: newValidationErrors}
    }

}