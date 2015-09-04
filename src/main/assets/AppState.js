import Bacon from 'baconjs'
import moment from 'moment-timezone'

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

  const serverUpdatesBus = new Bacon.Bus()
  const cssEffectsBus = new Bacon.Bus()
  const propertiesS = Bacon.fromPromise(HttpUtil.get(propertiesUrl))
  const userS = propertiesS.flatMap(initAuthentication)
  const countriesS = propertiesS
    .map('.koodistoCountriesUrl')
    .map(HttpUtil.get)
    .flatMapLatest(Bacon.fromPromise)
    .mapError(function(_) { return [] })
  const sessionS = userS.filter(isNotEmpty).flatMap(checkSession(sessionUrl))
  const hashS = userS.flatMap(locationHash).filter(isNotEmpty)
  cssEffectsBus.plug(hashS)

  const updateFieldS = dispatcher.stream(events.updateField).merge(serverUpdatesBus)

  const stateP = Bacon.update(initialState,
    [propertiesS, countriesS], onStateInit,
    [cssEffectsBus], onCssEffectValue,
    [userS], onLoginLogout,
    [sessionS], onSessionFromServer,
    [updateFieldS], onUpdateField)

  const formSubmittedS = stateP.sampledBy(dispatcher.stream(events.submitForm), (state, form) => ({state, form}))

  const userDataFormSubmittedP = formSubmittedS
    .filter(({form}) => form === 'userDataForm')
    .flatMapLatest(({state}) => submitUserDataToServer(state))
  serverUpdatesBus.plug(userDataFormSubmittedP)

  return stateP

  function onStateInit(state, properties, countries) {
    return {...state, properties, countries}
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

  function locationHash() {
    var currentHash = location.hash
    //location.hash = ""
    return Bacon.once(currentHash)
  }

  function isNotEmpty(x) { return !_.isEmpty(x) }
}

function checkSession(sessionUrl) {
  return (user) => Bacon.fromPromise(HttpUtil.post(sessionUrl, user)).skipErrors()
}

function submitUserDataToServer(state) {
  const userData = {
    email: state.session.email,
    idpentityid: state.session.idpentityid,
    firstName: state.firstName,
    lastName: state.lastName,
    birthDate: moment(state.birthDate, "DDMMYYYY").tz('Europe/Helsinki').format("YYYY-MM-DD") + "T00:00:00Z",
    personId: state.personId,
    gender: state.gender,
    nationality: state.nationality,
    educationLevel: state.educationLevel,
    educationCountry: state.educationCountry
  }
  return Bacon.fromPromise(HttpUtil.post(state.properties.userDataUrl, userData))
}