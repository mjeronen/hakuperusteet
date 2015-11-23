import React from 'react'
import _ from 'lodash'

import {createSelectOptions, mapAndSortKoodistoByLang} from '../util/HtmlUtils.js'
import {translation, resolveLang} from '../../assets-common/translations/translations.js'
import {tarjontaForHakukohdeOid} from "../util/TarjontaUtil.js"

export default class EducationLevel extends React.Component {
  constructor(props) {
    super()
    this.id = "educationLevel"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const state = this.props.state
    const allBaseEducations = (_.isEmpty(state.properties) || _.isEmpty(state.properties.baseEducation)) ? [] : state.properties.baseEducation
    const baseEducationsForCurrent = tarjontaForHakukohdeOid(state, state.hakukohdeOid).baseEducations
    const baseEducationOptions = allBaseEducations.filter(function(b) { return _.contains(baseEducationsForCurrent, b.id) })
    const result = createSelectOptions(mapAndSortKoodistoByLang(baseEducationOptions, resolveLang()))
    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.education.level") + " *"}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes} value={this.props.state[this.id]}>
        {result}
      </select>
      </div>
  }
}
