import React from 'react'
import _ from 'lodash'

import {createSelectOptions, mapKoodistoByLang} from '../util/HtmlUtils.js'
import {translation, resolveLang} from '../../assets-common/translations/translations.js'

export default class EducationCountry extends React.Component {
  constructor(props) {
    super()
    this.id = "educationCountry"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const countries = _.isEmpty(this.props.countries) ? {} : this.props.countries
    const result = createSelectOptions(mapKoodistoByLang(countries, resolveLang()))

    return <div className="userDataFormRow">
        <label htmlFor={this.id}>{translation("title.education.country") + " *"}</label>
        <select id={this.id} onChange={this.changes} onBlur={this.changes} value={this.props.state[this.id]}>
          {result}
        </select>
      </div>
  }
}
