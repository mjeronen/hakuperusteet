import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class Nationality extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("nationality", emptySelectValue())
  }
  render() {
    const field = "nationality"
    const controller = this.props.controller
    const result = createSelectOptions(this.props.countries)

    return <div className="userDataFormRow">
      <label htmlFor={field}>Nationality</label>
      <select id={field} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
