import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  render() {
    const controller = this.props.controller
    const state = this.props.state
    return <div>
        <label htmlFor="birthDate">Birth Date</label>
        <input type="text" id="birthDate" name="birthDate" onChange={controller.valueChanges}/>
        <br />
        <input type="checkbox" name="finnishSSN" id="finnishSSN" onChange={controller.checkedChanges} />
        <label htmlFor="hasFinnishSSH">I have Finnish Social Security Number</label>
        <br />
        { (!_.isUndefined(state.hasFinnishSSN) && state.hasFinnishSSN === true)
          ? <div>
            <label htmlFor="finnishSSN">SSN</label>
            <input type="text" id="finnishSSN" name="finnishSSN" onChange={controller.valueChanges}/>
          </div>
          : null
        }
      </div>
  }
}
