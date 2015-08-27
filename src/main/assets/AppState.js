import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './util/Dispatcher'
import {initAuthentication} from './util/GoogleAuthentication'
import {initChangeListeners} from './ChangeListeners'

const dispatcher = new Dispatcher()
const events = {
  updateField: 'updateField'
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
    [dispatcher.stream(events.updateField)], onUpdateField
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
}

function checkSession(sessionUrl) {
  return (user) => {
    if (_.isUndefined(user.email)) {
      return Bacon.once({})
    } else {
      return Bacon.fromPromise(HttpUtil.post(sessionUrl, user))
    }
  }
}

export function componentOnChangeListener(field, newValue) {
  dispatcher.push(events.updateField, { field: field, value: newValue })
}