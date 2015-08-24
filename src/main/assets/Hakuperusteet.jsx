import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './HttpUtil.js'
import FormController from './FormController.js'
import HakuperusteetPage from './HakuperusteetPage.jsx'

const propertiesUrl = "/hakuperusteet/api/v1/properties/"
const propertiesP = Bacon.fromPromise(HttpUtil.get(propertiesUrl))

function initApp() {
  const controller = new FormController({
    "propertiesP": propertiesP
  })
  const stateProperty = controller.initialize()
  const getReactComponent = function(state) {
    return <HakuperusteetPage controller={controller} state={state} />
  }
  return { stateProperty: stateProperty, getReactComponent: getReactComponent }
}

const app = initApp()
app.stateProperty.onValue((state) => {
  console.log("Updating UI with state:", state)
  try {
    React.render(app.getReactComponent(state), document.getElementById('app'))
  } catch (e) {
    console.log('Error from React.render with state', state, e)
  }
})

