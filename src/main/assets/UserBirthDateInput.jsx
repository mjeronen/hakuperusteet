import React from 'react'
import _ from 'lodash'

export default class UserBirthDateInput extends React.Component {
  render() {
    const controller = this.props.controller
    const state = this.props.state
    return <div>
        <label htmlFor="birthDate">Birth Date (ddmmyyyy)</label>
        <input type="text" id="birthDate" name="birthDate" onChange={controller.valueChanges}/>
        <br />
        <input type="checkbox" name="hasPersonId" id="hasPersonId" onChange={controller.checkedChanges} />
        <label htmlFor="hasPersonId">I have Finnish Social Security Number</label>
        <br />
        { (!_.isUndefined(state.hasPersonId) && state.hasPersonId === true)
          ? <div>
            <label htmlFor="personId">SSN</label>
            <input type="text" id="personId" name="personId" onChange={controller.valueChanges}/>
          </div>
          : null
        }
      </div>
  }
}
