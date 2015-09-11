import React from 'react'
import _ from 'lodash'

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
    return <div className="userDataFormRow">
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} />
        <label htmlFor="personId" className="ssnLabel">I have Finnish social security number</label>
        <input type="text" id={this.id} name="personId" onChange={controller.valueChanges} maxLength="5" />
        <span className="fieldFormatInfo">xxxxx</span>
      </div>
  }
}
