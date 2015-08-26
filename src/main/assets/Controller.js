import Bacon from 'baconjs'

import HttpUtil from './util/HttpUtil.js'
import Dispatcher from './Dispatcher'
import {initAuthentication} from './GoogleAuthentication'

const dispatcher = new Dispatcher()
const events = {
  initialState: 'initialState',
  updateField: 'updateField',
  signIn: 'signIn',
  signOut: 'signIn'
}

export default class Controller {
  constructor(props) {
    this.propertiesUrl = props.propertiesUrl
  }

  initialize() {
    const propertiesP = Bacon.fromPromise(HttpUtil.get(this.propertiesUrl))
    propertiesP.onValue(initAuthentication(dispatcher, events))

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
      [dispatcher.stream(events.updateField)], Controller.onUpdateField,
      [dispatcher.stream(events.signIn)], Controller.signIn,
      [dispatcher.stream(events.signOut)], Controller.signOut)

    return formFieldValuesP.filter((value) => { return !_.isEmpty(value) })
  }

  componentOnChangeListener(field, newValue) {
    dispatcher.push(events.updateField, Controller.createFieldUpdate(field, newValue))
  }

  static createFieldUpdate(field, value) {
    return { id: field.id, field: field, value: value }
  }

  static signIn(state, data) {
    console.log("signIn controller")
    console.log(data)
    return state
  }

  static signOut(state, data) {
    console.log("signOut controller")
    console.log(data)
    return state
  }


  static onInitialState(state, realInitialState) {
    return realInitialState
  }

  static onUpdateField(state, fieldUpdate) {
    state[fieldUpdate.field] = fieldUpdate.value
    return state
  }
}