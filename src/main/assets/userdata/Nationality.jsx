import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../translations/translations.js'

export default class Nationality extends React.Component {
  constructor(props) {
    super()
    this.id = "nationality"
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const controller = this.props.controller
    const countries = _.isEmpty(this.props.countries) ? {} : JSON.parse(this.props.countries)
    const result = createSelectOptions(countries)

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.nationality")}</label>
      <select id={this.id} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
