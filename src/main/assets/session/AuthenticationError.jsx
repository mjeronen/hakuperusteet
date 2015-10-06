import React from 'react'
import addons from 'react/addons'

var ReactTransitionGroup = React.addons.CSSTransitionGroup

import {translation} from '../translations/translations.js'

export default class AuthenticationError extends React.Component {
  render() {
    return <ReactTransitionGroup transitionName="loginerror-result" transitionAppear={true}>
      <div className="authentication-error">
        <p>{translation("errors.authentication.invalid")}</p>
      </div>
    </ReactTransitionGroup>
  }
}