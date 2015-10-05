import React from 'react'
import _ from 'lodash'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../translations/translations.js'

export default class EducationLevel extends React.Component {
  constructor(props) {
    super()
    this.id = "educationLevel"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const state = this.props.state
    const allBaseEducations = (_.isEmpty(state.properties) || _.isEmpty(state.properties.baseEducation)) ? [] : state.properties.baseEducation
    const baseEducationsForCurrent = _.isEmpty(state.tarjonta) ? [] : state.tarjonta.baseEducations
    const baseEducationOptions = allBaseEducations.filter(function(b) { return _.contains(baseEducationsForCurrent, b.id) })
    const result = createSelectOptions(baseEducationOptions)
    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.education.level")}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes}>
        {result}
      </select>
      </div>
  }
}
