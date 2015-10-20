import React from 'react'
import _ from 'lodash'

import {translation} from '../../assets-common/translations/translations.js'

export default class UserSSNInput extends React.Component {
  constructor(props) {
    super()
    this.id = "personId"
    this.changes = props.controller.valueChanges
    this.checkedChanges = props.controller.checkedChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) {
      this.changes({ target: { id: this.id, value: "" }})
      this.checkedChanges({ target: { id: "hasPersonId", checked: false }})
    } else {
      this.changes({ target: { id: this.id, value: this.props.state[this.id] }})
      this.checkedChanges({ target: { id: "hasPersonId", checked: true }})
    }
  }

  render() {
    const hasPersonId = (this.props.state.hasPersonId == true) ? true : false
    const disabled = hasPersonId ? "" : "disabled"
    const checked = hasPersonId ? true : null
    return <div className="userDataFormRow">
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={this.checkedChanges} checked={checked} />
        <label htmlFor="personId" className="ssnLabel">{translation("userdataform.personalId")}</label>
        <input type="text" id={this.id} name="personId" onChange={this.changes} onBlur={this.changes} disabled={disabled} maxLength="5" value={this.props.state[this.id]}/>
        <span className="fieldFormatInfo">xxxxx</span>
      </div>
  }

}
