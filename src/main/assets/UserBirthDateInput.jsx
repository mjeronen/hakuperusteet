import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  render() {
    const controller = this.props.controller
    const state = this.props.state
    return <div>
        <label htmlFor="birthDate">Birth Date</label>
        <input type="text" id="birthDate" name="birthDate" onChange={controller.valueChanges}/>
        <span className="birtDateFormatInfo">ddmmyyyy</span>
        <br />
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} />
        <label htmlFor="personId" className="ssnLabel">I have Finnish social security number</label>
        <input type="text" id="personId" name="personId" onChange={controller.valueChanges}/>
      </div>
  }
}
