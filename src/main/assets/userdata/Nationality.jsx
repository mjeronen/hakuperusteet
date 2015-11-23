import React from 'react'

import {createSelectOptions, mapAndSortKoodistoByLang} from '../util/HtmlUtils.js'
import {translation, resolveLang} from '../../assets-common/translations/translations.js'

export default class Nationality extends React.Component {
  constructor(props) {
    super()
    this.id = "nationality"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const countries = _.isEmpty(this.props.countries) ? {} : this.props.countries
    const selected = _.isEmpty(this.props.state) ? null : this.props.state[this.id]
    const result = createSelectOptions(mapAndSortKoodistoByLang(countries, resolveLang()))

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.nationality") + " *"}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes} value={selected}>
        {result}
      </select>
    </div>
  }
}
