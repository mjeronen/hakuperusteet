import React from 'react'

import {createSelectOptions, mapKoodistoByLang} from '../util/HtmlUtils.js'
import {translation, resolveLang} from '../../assets-common/translations/translations.js'

export default class NativeLanguage extends React.Component {
  constructor(props) {
    super()
    this.id = "nativeLanguage"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.changes({ target: { id: this.id, value: "" }})
  }

  render() {
    const languages = _.isEmpty(this.props.languages) ? {} : this.props.languages
    const selected = _.isEmpty(this.props.state) ? null : this.props.state[this.id]
    const result = createSelectOptions(mapKoodistoByLang(languages, resolveLang()))

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.native.language")  + " *"}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes} value={selected}>
        {result}
      </select>
    </div>
  }
}
