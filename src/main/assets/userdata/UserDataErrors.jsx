import React from 'react'

import {requiredFieldMissing} from './UserDataErrors.js'

import {translation} from '../../assets-common/translations/translations.js'

export default class UserDataErrors extends React.Component {
  render() {
    const state = this.props.state
    return <div className="userDataFormRow">
      { requiredFieldMissing(state, "personId") ? <span className="error">{translation("userdataform.errors.requiredPersonId")}</span> : null}
    </div>
  }
}
