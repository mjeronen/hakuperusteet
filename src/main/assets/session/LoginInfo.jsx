import React from 'react'

import {translation} from '../../assets-common/translations/translations.js'

export default class LoginInfo extends React.Component {
  render() {
    return <div className="login-info">
      <div dangerouslySetInnerHTML={{__html: translation("login.info.text")}}/>
    </div>
  }
}