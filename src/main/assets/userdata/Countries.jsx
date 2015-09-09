import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class Countries extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("educationCountry", emptySelectValue())
  }
  render() {
    const field = "educationCountry"
    const controller = this.props.controller
    const emptyCountries = [{ id: "", name: emptyValue}]
    const result = createSelectOptions(this.props.countries)

    return <div className="userDataFormRow">
        <label htmlFor={field}>Base education country</label>
        <select id={field} onChange={controller.valueChanges}>
          {result}
        </select>
      </div>
  }
}
