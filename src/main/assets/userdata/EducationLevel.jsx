import React from 'react'
import _ from 'lodash'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class EducationLevel extends React.Component {
  constructor(props) {
    super()
    this.id = "educationLevel"
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const baseEducations = this.props.state.tarjonta.baseEducations
    const result = createSelectOptions(this.props.state.properties.baseEducation,function(b) {
      return baseEducations.indexOf(b.id) !== -1
    })
    const controller = this.props.controller
    return <div className="userDataFormRow">
      <label htmlFor={this.id}>Base education level</label>
      <select id={this.id} onChange={controller.valueChanges}>
        {result}
      </select>
      </div>
  }
}
