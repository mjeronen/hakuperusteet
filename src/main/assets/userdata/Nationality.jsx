import React from 'react'

import {createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class Nationality extends React.Component {
  constructor(props) {
    super()
    this.id = "nationality"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.changes({ target: { id: this.id, value: "" }})
    else this.changes({ target: { id: this.id, value: this.props.state[this.id] }})
  }

  render() {
    const countries = _.isEmpty(this.props.countries) ? {} : this.props.countries
    const selected = _.isEmpty(this.props.state) ? null : this.props.state[this.id]
    const result = createSelectOptions(countries)

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.nationality") + " *"}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes} value={selected}>
        {result}
      </select>
    </div>
  }
}
