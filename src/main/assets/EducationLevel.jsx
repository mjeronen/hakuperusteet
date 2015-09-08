import React from 'react'
import _ from 'lodash'

export default class EducationLevel extends React.Component {
  render() {
    const field = "educationLevel"
    const controller = this.props.controller
    return <div className="userdataFormFow">
      <label htmlFor={field}>Base education level</label>
      <select id={field} onChange={controller.valueChanges}>
        <option name="">Choose...</option>
        <option name="bachelor">Bachelor's Degree</option>
        <option name="master">Master's Degree</option>
        <option name="other">Other</option>
      </select>
      </div>
  }
}
