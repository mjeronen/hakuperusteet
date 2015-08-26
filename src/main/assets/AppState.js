import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './Dispatcher'
import {initAuthentication} from './GoogleAuthentication'

const dispatcher = new Dispatcher()
const events = {
  updateField: 'updateField'
}

export function initAppState(props) {
  const {propertiesUrl} = props
  const initialState = {}

  const propertiesS =
    Bacon.fromPromise(HttpUtil.get(propertiesUrl))

  const userS =
    propertiesS.flatMap(initAuthentication)

  const countriesS =
    propertiesS
      .map('.koodistoCountriesUrl')
      .map(HttpUtil.get)
      .flatMapLatest(Bacon.fromPromise)

  return Bacon.update(initialState,
    [propertiesS, countriesS], onStateInit,
    [userS], onLoginLogout,
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
}

export function componentOnChangeListener(field, newValue) {
  dispatcher.push(events.updateField, { field: field, value: newValue })
}