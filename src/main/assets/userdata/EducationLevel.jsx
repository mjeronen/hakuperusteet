import React from 'react'
import _ from 'lodash'

export default class EducationLevel extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("educationLevel", "Choose...")
  }

  render() {
    const field = "educationLevel"
    const controller = this.props.controller
    return <div className="userDataFormRow">
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
