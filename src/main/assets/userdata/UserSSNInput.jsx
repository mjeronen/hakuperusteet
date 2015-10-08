import React from 'react'
import _ from 'lodash'

import {translation} from '../../assets-common/translations/translations.js'

export default class UserBirthDateInput extends React.Component {
  constructor(props) {
    super()
    this.id = "personId"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    //this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const controller = this.props.controller
    const disabled = (this.props.state.hasPersonId == true) ? "" : "disabled"
    return <div className="userDataFormRow">
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} checked={this.props.state[this.name]?true:null} />
        <label htmlFor="personId" className="ssnLabel">{translation("userdataform.personalId")}</label>
        <input type="text" id={this.id} name="personId" onChange={this.changes} onBlur={this.changes} disabled={disabled} maxLength="5" value={this.props.state[this.id]}/>
        <span className="fieldFormatInfo">xxxxx</span>
      </div>
  }
}
