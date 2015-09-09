import React from 'react'

const emptyValue = "Choose..."

export default class NativeLanguage extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("nativeLanguage", emptyValue)
  }
  render() {
    const controller = this.props.controller
    const field = "nativeLanguage"
    const emptyLanguages = [{ id: "", name: emptyValue}]
    const languages = this.props.languages ? JSON.parse(this.props.languages) : emptyLanguages
    var toOptions = function (language) { return <option value={language.id} key={language.id}>{language.name}</option> }
    const result = languages.map(toOptions)
    result.unshift(<option value="" key="-">{emptyValue}</option>)
    return <div className="userDataFormRow">
      <label htmlFor={field}>Native language</label>
      <select id={field} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
