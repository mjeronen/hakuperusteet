import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class NativeLanguage extends React.Component {
  constructor(props) {
    super()
    this.id = "nativeLanguage"
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.id, value: emptySelectValue() }})
  }

  render() {
    const controller = this.props.controller
    const languages = _.isEmpty(this.props.languages) ? {} : JSON.parse(this.props.languages)
    const result = createSelectOptions(languages)

    return <div className="userDataFormRow">
      <label htmlFor={this.id}>Native language</label>
      <select id={this.id} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
