import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './util/Dispatcher'
import {initGoogleAuthentication} from './session/GoogleAuthentication'
import {isLoginToken, initEmailAuthentication} from './session/EmailAuthentication'
import {initChangeListeners} from './util/ChangeListeners'
import {parseNewValidationErrors} from './util/FieldValidator.js'
import {submitUserDataToServer} from './userdata/UserDataForm.js'
import {submitEducationDataToServer} from './education/EducationForm.js'

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
  const {tarjontaUrl, propertiesUrl, sessionUrl, authenticationUrl} = props
  const initialState = {}

  const gapiLoading = Bacon.fromPoll(10, checkGapiStatus).filter(skipLoadingMessages)
  const serverUpdatesBus = new Bacon.Bus()
  const cssEffectsBus = new Bacon.Bus()
  const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
  const hakukohdeS = Bacon.once(parseAoId()).toProperty()
  const tarjontaS = hakukohdeS.flatMap(fetchFromTarjonta).toEventStream()

  const hashS = propertiesS.flatMap(locationHash).filter(isNotEmpty)
  const sessionS = propertiesS.flatMap(sessionFromServer(sessionUrl))
  const emailUserS = hashS.filter(isLoginToken).flatMap(initEmailAuthentication)
  const googleUserS = gapiLoading.concat(propertiesS.toProperty()).flatMap(initGoogleAuthentication)

  const credentialsS = Bacon.update({},
    [sessionS], handleSessionUserEvent,
    [emailUserS], handleEmailUserEvent,
    [googleUserS], handleGoogleUserEvent
  ).skipDuplicates().toEventStream()

  const sessionDataS = credentialsS.filter(isNotEmpty).flatMap(authenticate(authenticationUrl)).doAction(sessionInit)
  cssEffectsBus.plug(hashS.filter(isCssEffect).flatMap(toCssEffect))

  const updateFieldS = dispatcher.stream(events.updateField).merge(serverUpdatesBus)
  const fieldValidationS = dispatcher.stream(events.fieldValidation)
  const logOutS = dispatcher.stream(events.logOut)

  const stateP = Bacon.update(initialState,
    [propertiesS, hakukohdeS], onStateInit,
    [tarjontaS], onTarjontaValue,
    [cssEffectsBus], onCssEffectValue,
    [credentialsS], onCredentialsChange,
    [sessionDataS], onSessionDataFromServer,
    [updateFieldS], onUpdateField,
    [logOutS], onLogOut,
    [fieldValidationS], onFieldValidation)

  const formSubmittedS = stateP.sampledBy(dispatcher.stream(events.submitForm), (state, form) => ({state, form}))

  const userDataFormSubmittedP = formSubmittedS
    .filter(({form}) => form === 'userDataForm')
    .flatMapLatest(({state}) => submitUserDataToServer(state))
  serverUpdatesBus.plug(userDataFormSubmittedP)

  const educationFromSubmittedP = formSubmittedS
    .filter(({form}) => form === 'educationForm')
    .flatMapLatest(({state}) => submitEducationDataToServer(state))
  serverUpdatesBus.plug(educationFromSubmittedP)

  return stateP

  function handleSessionUserEvent(_, newSessionUser) {
    return newSessionUser
  }

  function handleGoogleUserEvent(currentUser, newGoogleUser) {
    if(_.isEmpty(currentUser)) return newGoogleUser
    if(currentUser.idpentityid === "google" && currentUser.token === newGoogleUser.token) return currentUser
    if(currentUser.idpentityid === "google") return newGoogleUser
    return currentUser
  }

  function handleEmailUserEvent(currentUser, newEmailUser) {
    if(_.isEmpty(currentUser)) return newEmailUser
    if(currentUser.idpentityid === "oppijaToken") return newEmailUser
    return currentUser
  }

  function onStateInit(state, properties, hakukohdeOid) {
    return {...state, properties, hakukohdeOid}
  }

  function onTarjontaValue(state, tarjonta) {
    const currentTarjonta = state.tarjonta || []
    const newTarjonta = {...currentTarjonta, [tarjonta.hakukohdeOid]: tarjonta}
    return {...state, ['tarjonta']: newTarjonta}
  }

  function onCssEffectValue(state, effect) {
    if (effect !== "") {
      Bacon.once("").take(1).delay(3000).onValue((x) => cssEffectsBus.push(x))
    }
    return {...state, effect}
  }

  function onCredentialsChange(state, credentials) {
    return {...state, credentials}
  }

  function onUpdateField(state, {field, value}) {
    return {...state, [field]: value}
  }

  function onLogOut(state, _) {
    return {...state, ['credentials']: {}, ['sessionData']: {}}
  }

  function onSessionDataFromServer(state, sessionData) {
    return {...state, sessionData}
  }

  function onFieldValidation(state, {field, value}) {
    const newValidationErrors = parseNewValidationErrors(state, field, value)
    return {...state, ['validationErrors']: newValidationErrors}
  }

  function locationHash() {
    var currentHash = location.hash
    location.hash = ""
    return Bacon.once(currentHash)
  }
  function isNotEmpty(x) { return !_.isEmpty(x) }
  function isCssEffect(x) { return _.startsWith(x, "#/effect/") }
  function toCssEffect(x) { return x.replace("#/effect/", "") }
  function checkGapiStatus() {
    if (typeof gapi == "undefined") return new Bacon.Next("loading")
    else return new Bacon.End()
  }
  function skipLoadingMessages(x) { return x != "loading" }
  function parseAoId() {
    const aoid = /\/hakuperusteet\/ao\/([0-9\.]*)$/
    return aoid.test(location.pathname) ? aoid.exec(location.pathname).pop() : "1.2.246.562.20.69046715533"
  }
  function fetchFromTarjonta(hakukohde) {
    return Bacon.fromPromise(HttpUtil.get(tarjontaUrl + "/" + hakukohde))
  }
}

function authenticate(authenticationUrl) {
  return (user) => Bacon.fromPromise(HttpUtil.post(authenticationUrl, user)).doAction(removeAuthenticationError).doError(handleAuthenticationError).skipErrors()
}

function sessionFromServer(sessionUrl) {
  return (user) => Bacon.fromPromise(HttpUtil.get(sessionUrl, user)).doError(sessionInit).skipErrors()
}

function removeAuthenticationError(_) {
  dispatcher.push(events.updateField, {field: 'authenticationError', value: false})
}

function handleAuthenticationError(e) {
  if (e.status == 401) {
    dispatcher.push(events.updateField, {field: 'authenticationError', value: true})
  }
}

function sessionInit() {
  window.SESSION_INITED_FOR_TESTING = true
  dispatcher.push(events.updateField, {field: 'sessionInit', value: true})
}
