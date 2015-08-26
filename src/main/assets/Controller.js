import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './Dispatcher'
import GoogleAuthentication from './GoogleAuthentication'

const dispatcher = new Dispatcher()
const events = {
  initialState: 'initialState',
  updateField: 'updateField'
}

export default class Controller {
  constructor(props) {
    this.propertiesUrl = props.propertiesUrl
  }

  initialize() {
    const propertiesP = Bacon.fromPromise(HttpUtil.get(this.propertiesUrl))
    const auth = new GoogleAuthentication(dispatcher)
    propertiesP.onValue(auth.initialize)

    const countriesFromKoodisto = function(props) { return Bacon.fromPromise(HttpUtil.get(props.koodistoCountriesUrl)) }
    const countriesP = propertiesP.flatMap(countriesFromKoodisto)

    const initialStateTemplate = {
      properties: propertiesP,
      countries: countriesP
    }
    const initialState = Bacon.combineTemplate(initialStateTemplate)
    initialState.onValue(function(state) { dispatcher.push(events.initialState, state) })

    const formFieldValuesP = Bacon.update({},
      [dispatcher.stream(events.initialState)], Controller.onInitialState,
      [dispatcher.stream(events.updateField)], Controller.onUpdateField)

    return formFieldValuesP.filter((value) => { return !_.isEmpty(value) })
  }

  componentOnChangeListener(field, newValue) {
    dispatcher.push(events.updateField, Controller.createFieldUpdate(field, newValue))
  }

  static createFieldUpdate(field, value) {
    return { id: field.id, field: field, value: value }
  }

  static onInitialState(state, realInitialState) {
    return realInitialState
  }

  static onUpdateField(state, fieldUpdate) {
    state[fieldUpdate.field] = fieldUpdate.value
    return state
  }
}