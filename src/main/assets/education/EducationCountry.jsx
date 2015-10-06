import React from 'react'
import _ from 'lodash'

import {createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export default class EducationCountry extends React.Component {
  constructor(props) {
    super()
    this.id = "educationCountry"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const countries = _.isEmpty(this.props.countries) ? {} : this.props.countries
    const result = createSelectOptions(countries)

    return <div className="userDataFormRow">
        <label htmlFor={this.id}>{translation("title.education.country") + " *"}</label>
        <select id={this.id} onChange={this.changes} onBlur={this.changes}>
          {result}
        </select>
      </div>
  }
}
