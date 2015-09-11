import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  constructor() {
    super()
    this.id = "birthDate"
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.id, value: "" }})
  }

  render() {
    const controller = this.props.controller
    return <div className="userDataFormRow">
        <label htmlFor="birthDate">Birth Date</label>
        <input type="text" id={this.id} name="birthDate" onChange={controller.valueChanges} maxLength="8" />
        <span className="birtDateFormatInfo">ddmmyyyy</span>
      </div>
  }
}
