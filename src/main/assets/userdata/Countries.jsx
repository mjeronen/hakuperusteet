import React from 'react'

const emptyValue = "Choose..."

export default class Countries extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("educationCountry", emptyValue)
  }
  render() {
    const field = "educationCountry"
    const controller = this.props.controller
    const emptyCountries = [{ id: "", name: emptyValue}]
    const countries = this.props.countries ? JSON.parse(this.props.countries) : emptyCountries
    var toOptions = function (country) { return <option value={country.id} key={country.id}>{country.name}</option> }
    const result = countries.map(toOptions)
    result.unshift(<option value="" key="-">{emptyValue}</option>)
    return <div className="userDataFormRow">
        <label htmlFor={field}>Base education country</label>
        <select id={field} onChange={controller.valueChanges}>
          {result}
        </select>
      </div>
  }
}
