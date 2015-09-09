import React from 'react'

export default class Nationality extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("nationality","Choose..")
  }

  render() {
    const controller = this.props.controller
    const field = "nationality"
    const emptyCountries = [{ id: "", name: "Choose.."}]
    const countries = this.props.countries ? JSON.parse(this.props.countries) : emptyCountries
    var toOptions = function (country) { return <option value={country.id} key={country.id}>{country.name}</option> }
    const result = countries.map(toOptions)
    result.unshift(<option value="" key="-">Choose..</option>)
    return <div className="userDataFormRow">
      <label htmlFor={field}>Nationality</label>
      <select id={field} onChange={controller.valueChanges}>
          {result}
      </select>
    </div>
  }
}
