import React from 'react'

import {translation} from '../../assets-common/translations/translations.js'

export default class LoginInfo extends React.Component {
  render() {
    return <div className="login-info">
      <p>{translation("login.info.text")}</p>
    </div>
  }
}