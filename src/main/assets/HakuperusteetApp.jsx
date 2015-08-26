import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import {initAppState, componentOnChangeListener} from './AppState.js'
import HakuperusteetPage from './HakuperusteetPage.jsx'

const appState = initAppState({
  propertiesUrl: "/hakuperusteet/api/v1/properties/",
  sessionUrl: "/hakuperusteet/api/v1/session/"
})

appState
  .filter(state => !_.isEmpty(state))
  .onValue((state) => {
    const getReactComponent = function(state) {
      return <HakuperusteetPage controller={componentOnChangeListener} state={state} />
    }
    console.log("Updating UI with state:", state)
    try {
      React.render(getReactComponent(state), document.getElementById('app'))
    } catch (e) {
      console.log('Error from React.render with state', state, e)
    }
  })
