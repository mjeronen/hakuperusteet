import React from 'react'
import _ from 'lodash'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class EducationLevel extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("educationLevel", emptySelectValue())
  }

  render() {
    const field = "educationLevel"
    const baseEducations = this.props.state.tarjonta.baseEducations
    const result = createSelectOptions(this.props.state.properties.baseEducation,function(b) {
      return baseEducations.indexOf(b.id) !== -1
    })
    const controller = this.props.controller
    return <div className="userDataFormRow">
      <label htmlFor={field}>Base education level</label>
      <select id={field} onChange={controller.valueChanges}>
        {result}
      </select>
      </div>
  }

}
