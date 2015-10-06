import React from 'react'

import * as errors from './UserDataErrors.js'

import {translation} from '../../assets-common/translations/translations.js'

export default class UserDataErrors extends React.Component {
  render() {
    const state = this.props.state
    return <div className="userDataFormRow">
      { errors.requiredPersonIdMissing(state) ? <span className="error">{translation("userdataform.errors.requiredPersonId")}</span> : null}
    </div>
  }
}
