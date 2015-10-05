import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HakuperusteetAdminPage from './HakuperusteetAdminPage.jsx'

const getReactComponent = function() {
    return <HakuperusteetAdminPage />
}
console.log("Updating UI with state:")
try {
    React.render(getReactComponent(), document.getElementById('app'))
} catch (e) {
    console.log('Error from React.render with state', e)
}