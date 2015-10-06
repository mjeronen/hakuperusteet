import React from 'react'

import {translation} from '../translations/translations.js'

export default class AuthenticationError extends React.Component {
  render() {
    return <div className="authentication-error">
      <p>{translation("errors.authentication.invalid")}</p>
    </div>
  }
}