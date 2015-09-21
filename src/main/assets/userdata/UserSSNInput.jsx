import React from 'react'
import _ from 'lodash'

import {translation} from '../translations/translations.js'

export default class UserBirthDateInput extends React.Component {
  constructor(props) {
    super()
    this.id = "personId"
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.id, value: "" }})
  }

  render() {
    const controller = this.props.controller
    const disabled = (this.props.state.hasPersonId == true) ? "" : "disabled"
    return <div className="userDataFormRow">
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} />
        <label htmlFor="personId" className="ssnLabel">{translation("userdataform.personalId")}</label>
        <input type="text" id={this.id} name="personId" onChange={controller.valueChanges} disabled={disabled} maxLength="5" />
        <span className="fieldFormatInfo">xxxxx</span>
      </div>
  }
}
