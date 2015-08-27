import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './util/Dispatcher'
import {initAuthentication} from './util/GoogleAuthentication'
import {initChangeListeners} from './util/ChangeListeners'

const dispatcher = new Dispatcher()
const events = {
  updateField: 'updateField',
  submitForm: 'submitForm'
}

export function changeListeners() {
  return initChangeListeners(dispatcher, events)
}

export function initAppState(props) {
  const {propertiesUrl, sessionUrl} = props
  const initialState = {}

  const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
  const userS = propertiesS.flatMap(initAuthentication)
  const countriesS = propertiesS
      .map('.koodistoCountriesUrl')
      .map(HttpUtil.get)
      .flatMapLatest(Bacon.fromPromise)
  const sessionS = userS.flatMap(checkSession(sessionUrl))

  return Bacon.update(initialState,
    [propertiesS, countriesS], onStateInit,
    [userS], onLoginLogout,
    [sessionS], onSessionFromServer,
    [dispatcher.stream(events.updateField)], onUpdateField,
    [dispatcher.stream(events.submitForm)], onSubmitForm
  )

  function onStateInit(state, properties, countries) {
    return {...state, properties, countries}
  }

  function onLoginLogout(state, user) {
    return {...state, user}
  }

  function onUpdateField(state, {field, value}) {
    return {...state, [field]: value}
  }

  function onSessionFromServer(state, sessionData) {
    if (_.isUndefined(sessionData.email)) {
      delete state['sessionData']
      return state
    } else {
      return {...state, sessionData}
    }
  }

  function onSubmitForm(state, form) {
    if (form === "userDataForm") {
      handleUserDataSubmit(state, state.properties.userDataUrl)
    }
    return state
  }
}

function checkSession(sessionUrl) {
  return (user) => (_.isUndefined(user.email)) ? Bacon.once({}) : Bacon.fromPromise(HttpUtil.post(sessionUrl, user))
}

function handleUserDataSubmit(state, userDataUrl) {
  const userData = {
    firstName: state.firstName,
    lastName: state.lastName,
    birthDate: state.birthDate,
    finnishSSN: state.finnishSSN,
    nationality: state.nationality,
    educationLevel: state.educationLevel,
    country: state.country
  }
  Bacon.fromPromise(HttpUtil.post(userDataUrl, userData)).onValue((result) => {
    dispatcher.push(events.updateField, { field: 'henkiloOid', value: result.henkiloOid })
  })
}