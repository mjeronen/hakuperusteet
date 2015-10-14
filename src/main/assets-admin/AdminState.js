import Bacon from 'baconjs'
import _ from 'lodash'

import {submitUserDataToServer} from './userdata/UserDataForm.js'
import {submitEducationDataToServer} from './education/EducationForm.js'
import HttpUtil from '../assets/util/HttpUtil.js'
import Dispatcher from '../assets/util/Dispatcher'
import {initChangeListeners} from '../assets/util/ChangeListeners'
import {parseNewValidationErrors} from '../assets/util/FieldValidator.js'
import {enableSubmitAndHideBusy} from '../assets/util/HtmlUtils.js'

const dispatcher = new Dispatcher()
const events = {
    updateEducationForm: 'updateEducationForm',
    route: 'route',
    search: 'search',
    updateField: 'updateField',
    submitForm: 'submitForm',
    fieldValidation: 'fieldValidation',
    logOut: 'logOut'
}

export function changeListeners() {
    return initChangeListeners(dispatcher, events)
}

export function initAppState(props) {
    const {tarjontaUrl, propertiesUrl, usersUrl, userUpdateUrl, applicationObjectUpdateUrl} = props
    const initialState = {['userUpdateUrl']:userUpdateUrl, ['applicationObjectUpdateUrl']:applicationObjectUpdateUrl}
    const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
    const serverUpdatesBus = new Bacon.Bus()
    const hakukohdeS = Bacon.once("1.2.246.562.20.69046715533")
    const tarjontaS = hakukohdeS.flatMap(fetchFromTarjonta).toEventStream()

    const searchS = Bacon.mergeAll(dispatcher.stream(events.search),Bacon.once("")).skipDuplicates(_.isEqual)
    const fetchUsersFromServerS =
      searchS.flatMap(search => Bacon.fromPromise(HttpUtil.get(`/hakuperusteetadmin/api/v1/admin?search=${search}`)))

    const updateRouteS = Bacon.mergeAll(dispatcher.stream(events.route),Bacon.once(document.location.pathname))
        .map(personOidInUrl)
        .skipDuplicates(_.isEqual)
        .flatMap(function(uniquePersonOid) {
            return Bacon.fromPromise(HttpUtil.get(`/hakuperusteetadmin/api/v1/admin/${uniquePersonOid}`))
        })

    const updateFieldS = dispatcher.stream(events.updateField).merge(serverUpdatesBus)

    const fieldValidationS = dispatcher.stream(events.fieldValidation)
    const updateEducationFormS = dispatcher.stream(events.updateEducationForm)
    const stateP = Bacon.update(initialState,
        [propertiesS], onStateInit,
        [fetchUsersFromServerS], onSearchUpdate,
        [tarjontaS], onTarjontaValue,
        [updateRouteS],onUpdateUser,
        [updateEducationFormS], onUpdateEducationForm,
        [updateFieldS], onUpdateField,
        [fieldValidationS], onFieldValidation)

    const formSubmittedS = stateP.sampledBy(dispatcher.stream(events.submitForm), (state, form) => ({state, form}))
    const userDataFormSubmitS = formSubmittedS.filter(({form}) => form === 'userDataForm').flatMapLatest(({state}) => submitUserDataToServer(state))

    userDataFormSubmitS.onValue((_) => {
        const form = document.getElementById('userDataForm')
        enableSubmitAndHideBusy(form)
    })
    serverUpdatesBus.plug(userDataFormSubmitS)

    const educationFormSubmitS = formSubmittedS.filter(({form}) => form.match(new RegExp("educationForm_(.*)"))).flatMapLatest(({state, form}) => {
      const hakukohdeOid = form.match(new RegExp("educationForm_(.*)"))[1]
      const applicationObject = _.find(state.applicationObjects, ao => ao.hakukohdeOid === hakukohdeOid)
      return submitEducationDataToServer(state, applicationObject, document.getElementById(form))
    });
    educationFormSubmitS.onValue((result) => {
        const form = document.getElementById('educationForm_'+ result.hakukohdeOid)
        enableSubmitAndHideBusy(form)
    })
    serverUpdatesBus.plug(educationFormSubmitS)

    return stateP

    function onSearchUpdate(state, users) {
        return {...state, ['users']: users}
    }
    function onUpdateEducationForm(state, newAo) {
        var updatedAos = _.map(state.applicationObjects, (oldAo => oldAo.id == newAo.id ? newAo : oldAo))
        return {...state, ['applicationObjects']: updatedAos}
    }
    function onTarjontaValue(state, tarjonta) {
        const currentTarjonta = state.tarjonta || []
        const newTarjonta = {...currentTarjonta, [tarjonta.hakukohdeOid]: tarjonta}
        return {...state, ['tarjonta']: newTarjonta}
    }
    function onUpdateField(state, {field, value}) {
        return {...state, [field]: value}
    }
    function onUpdateUser(state, user) {
        return {...state, ...user.user, ['applicationObjects']: user.applicationObject, ['fromServer']: user}
    }
    function onStateInit(state, properties) {
        return {...state, properties}
    }
    function onFieldValidation(state, {field, value}) {
        const newValidationErrors = parseNewValidationErrors(state, field, value)
        const validationErrors = {...newValidationErrors, ['noChanges']: _.isMatch(state, state.fromServer.user) ? "required" : null}
        return {...state, ...{'validationErrors' : validationErrors}}
    }
    function fetchFromTarjonta(hakukohde) {
        return Bacon.fromPromise(HttpUtil.get(tarjontaUrl + "/" + hakukohde))
    }
    function personOidInUrl(url) {
        var match = url.match(new RegExp("oppija/(.*)"))
        return match ? match[1] : null
    }
}