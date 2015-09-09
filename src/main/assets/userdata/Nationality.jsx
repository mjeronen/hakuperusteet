import React from 'react'

const emptyValue = "Choose..."

export default class Nationality extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("nationality", emptyValue)
  }
  render() {
    const field = "nationality"
    const controller = this.props.controller
    const emptyCountries = [{ id: "", name: emptyValue}]
    const countries = this.props.countries ? JSON.parse(this.props.countries) : emptyCountries
    var toOptions = function (country) { return <option value={country.id} key={country.id}>{country.name}</option> }
    const result = countries.map(toOptions)
    result.unshift(<option value="" key="-">{emptyValue}</option>)
    return <div className="userDataFormRow">
      <label htmlFor={field}>Nationality</label>
      <select id={field} onChange={controller.valueChanges}>
        {result}
      </select>
    </div>
  }
}
