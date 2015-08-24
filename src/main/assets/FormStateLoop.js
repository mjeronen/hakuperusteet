import Bacon from 'baconjs'
import _ from 'lodash'
import Immutable from 'seamless-immutable'

import HttpUtil from './HttpUtil.js'
import FormStateTransitions from './FormStateTransitions.js'

export default class FormStateLoop {
  constructor(dispatcher, events) {
    this.dispatcher = dispatcher
    this.events = events
  }

  initialize(controller) {
    const dispatcher = this.dispatcher
    const events = this.events

    const countriesFromKoodisto = function(props) { return Bacon.fromPromise(HttpUtil.get(props.koodistoCountriesUrl)) }
    const countriesUrlP = controller.propertiesP.flatMap(countriesFromKoodisto)
    const initialStateTemplate = {
      properties: controller.propertiesP,
      countries: countriesUrlP
    }
    const initialState = Bacon.combineTemplate(initialStateTemplate)
    initialState.onValue(function(state) {
      dispatcher.push(events.initialState, state)
    })

    const stateTransitions = new FormStateTransitions(dispatcher, events)
    const formFieldValuesP = Bacon.update({},
      [dispatcher.stream(events.initialState)], stateTransitions.onInitialState
    )
    return formFieldValuesP.filter((value) => { return !_.isEmpty(value) })
  }
}