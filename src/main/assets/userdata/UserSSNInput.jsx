import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("personId", "")
  }

  render() {
    const controller = this.props.controller
    const state = this.props.state
    return <div className="userDataFormRow">
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} />
        <label htmlFor="personId" className="ssnLabel">I have Finnish social security number</label>
        <input type="text" id="personId" name="personId" onChange={controller.valueChanges}/>
      </div>
  }
}
