import React from 'react'

import LocalizedText from '../translations/LocalizedText.jsx'

export default class LoginInfo extends React.Component {
  render() {
    return <div className="login-info">
      <p><LocalizedText translationKey="login.info.p" /></p>
    </div>
  }
}