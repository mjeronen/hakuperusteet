import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("birthDate", "")
  }

  render() {
    const controller = this.props.controller
    const state = this.props.state
    return <div className="userDataFormRow">
        <label htmlFor="birthDate">Birth Date</label>
        <input type="text" id="birthDate" name="birthDate" onChange={controller.valueChanges}/>
        <span className="birtDateFormatInfo">ddmmyyyy</span>
      </div>
  }
}
