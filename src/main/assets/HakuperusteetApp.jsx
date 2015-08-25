import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Controller from './Controller.js'
import HakuperusteetPage from './HakuperusteetPage.jsx'

const controller = new Controller({
  propertiesUrl: "/hakuperusteet/api/v1/properties/"
})
const appState = controller.initialize()

appState.onValue((state) => {
  const getReactComponent = function(state) {
    return <HakuperusteetPage controller={controller} state={state} />
  }
  console.log("Updating UI with state:", state)
  try {
    React.render(getReactComponent(state), document.getElementById('app'))
  } catch (e) {
    console.log('Error from React.render with state', state, e)
  }
})
