import React from 'react'
import _ from 'lodash'

import {translation} from '../../assets-common/translations/translations.js'

export default class UserBirthDateInput extends React.Component {
  constructor(props) {
    super()
    this.id = "birthDate"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    return <div className="userDataFormRow">
        <label htmlFor="birthDate">{translation("title.birth.date") + " *"}</label>
        <input type="text" id={this.id} name={this.id} onChange={this.changes} onBlur={this.changes} maxLength="8" />
        <span className="fieldFormatInfo">ddmmyyyy</span>
      </div>
  }
}
