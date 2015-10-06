import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class Nationality extends React.Component {
  constructor(props) {
    super()
    this.id = "nationality"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const controller = this.props.controller
    const countries = _.isEmpty(this.props.countries) ? {} : this.props.countries
    const result = createSelectOptions(countries)

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.nationality")}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes}>
        {result}
      </select>
    </div>
  }
}
