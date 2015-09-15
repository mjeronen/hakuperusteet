import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './util/Dispatcher'
import {initGoogleAuthentication} from './session/GoogleAuthentication'
import {initEmailAuthentication} from './session/EmailAuthentication'
import {initChangeListeners} from './util/ChangeListeners'
import {parseNewValidationErrors} from './util/FieldValidator.js'
import {submitUserDataToServer} from './util/UserDataForm.js'

const dispatcher = new Dispatcher()
const events = {
  updateField: 'updateField',
  submitForm: 'submitForm',
  fieldValidation: 'fieldValidation'
}

export function changeListeners() {
  return initChangeListeners(dispatcher, events)
}

export function initAppState(props) {
  const {tarjontaUrl, propertiesUrl, sessionDataUrl, authenticationUrl} = props
  const initialState = {}

  const serverUpdatesBus = new Bacon.Bus()
  const cssEffectsBus = new Bacon.Bus()
  const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
  const tarjontaS = Bacon.fromPromise(HttpUtil.get(tarjontaUrl))

  const hashS = propertiesS.flatMap(locationHash).filter(isNotEmpty)
  const googleUserS = propertiesS.flatMap(initGoogleAuthentication).toProperty("")
  const emailUserS = hashS.flatMap(initEmailAuthentication).toProperty("")
  const sessionDataS = propertiesS.flatMap(sessionData(sessionDataUrl))
  const userS = googleUserS
    .combine(emailUserS, selectAuthenticationData)
    .combine(sessionDataS, selectAuthenticationData)
    .toEventStream()
  const sessionS = userS.filter(isNotEmpty).flatMap(authenticate(authenticationUrl))
  cssEffectsBus.plug(hashS)

  const updateFieldS = dispatcher.stream(events.updateField).merge(serverUpdatesBus)
  const fieldValidationS = dispatcher.stream(events.fieldValidation)

  const stateP = Bacon.update(initialState,
    [propertiesS, tarjontaS], onStateInit,
    [cssEffectsBus], onCssEffectValue,
    [userS], onLoginLogout,
    [sessionS], onSessionFromServer,
    [updateFieldS], onUpdateField,
    [fieldValidationS], onFieldValidation)

  const formSubmittedS = stateP.sampledBy(dispatcher.stream(events.submitForm), (state, form) => ({state, form}))

  const userDataFormSubmittedP = formSubmittedS
    .filter(({form}) => form === 'userDataForm')
    .flatMapLatest(({state}) => submitUserDataToServer(state))
  serverUpdatesBus.plug(userDataFormSubmittedP)

  return stateP

  function onStateInit(state, properties, tarjonta) {
    return {...state, properties, tarjonta}
  }

  function onCssEffectValue(state, effect) {
    if (effect !== "") {
      Bacon.once("").take(1).delay(3000).onValue(function(x) {  cssEffectsBus.push(x) })
    }
    return {...state, effect}
  }

  function onLoginLogout(state, session) {
    return {...state, session}
  }

  function onUpdateField(state, {field, value}) {
    return {...state, [field]: value}
  }

  function onSessionFromServer(state, sessionData) {
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

  function selectAuthenticationData(left, right) {
    if (left.token != undefined) return left
    if (right.token != undefined) return right
    return {}
  }
  function isNotEmpty(x) { return !_.isEmpty(x) }
}

function authenticate(authenticationUrl) {
  return (user) => Bacon.fromPromise(HttpUtil.post(authenticationUrl, user)).skipErrors()
}

function sessionData(sessionDataUrl) {
  return (user) => Bacon.fromPromise(HttpUtil.get(sessionDataUrl, user)).skipErrors()
}
