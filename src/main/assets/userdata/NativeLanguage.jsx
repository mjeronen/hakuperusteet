import React from 'react'

import {emptySelectValue, createSelectOptions} from '../util/HtmlUtils.js'

export default class NativeLanguage extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("nativeLanguage", emptySelectValue())
  }
  render() {
    const controller = this.props.controller
    const field = "nativeLanguage"
    const result = createSelectOptions(this.props.languages)

    return <div className="userDataFormRow">
      <label htmlFor={field}>Native language</label>
      <select id={field} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
