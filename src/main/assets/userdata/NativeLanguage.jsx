import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'
import {translation} from '../translations/translations.js'

export default class NativeLanguage extends React.Component {
  constructor(props) {
    super()
    this.id = "nativeLanguage"
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const languages = _.isEmpty(this.props.languages) ? {} : this.props.languages
    const result = createSelectOptions(languages)

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>{translation("title.native.language")}</label>
      <select id={this.id} onChange={this.changes} onBlur={this.changes}>
        {result}
      </select>
    </div>
  }
}
