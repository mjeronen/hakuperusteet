import React from 'react'

import {requiredField, invalidField} from '../util/FieldValidator.js'

import {translation} from '../../assets-common/translations/translations.js'

export default class EducationErrors extends React.Component {
  render() {
    const state = this.props.state
    return <div className="userDataFormRow">
      { requiredField(state, "educationLevel") ? <span className="error">{translation("educationForm.errors.requiredEducationLevel")}</span> : null}
      { requiredField(state, "educationCountry") ? <span className="error">{translation("educationForm.errors.requiredEducationCountry")}</span> : null}
    </div>
  }
}
