import React from 'react'
import _ from 'lodash'

export default class Countries extends React.Component {
  constructor(props) {
    super(props)
  }
  render() {
    const field = "educationCountry"
    const controller = this.props.controller
    const lang = this.props.lang.toUpperCase()
    const emptyCountries = [{ id: "", name: "Choose.."}]
    const countries =  _.isUndefined(this.props.countries) ? emptyCountries : this.props.countries

    var parseValues = function (country) {
      if (_.isUndefined(country.metadata)) return { }
      const name = country.metadata.filter(function (meta) { return meta.kieli == lang})[0].nimi
      return { id: country.koodiArvo, name: name }
    }
    var formatted = countries.map(parseValues).filter(function(c) { return !_.isUndefined(c.id)} )

    var sortWith = function(n) { return n.name }
    var toOptions = function (country) { return <option value={country.id} key={country.id}>{country.name}</option> }
    const result = _.sortBy(formatted, sortWith).map(toOptions)
    result.unshift(<option value="" key="-">Choose..</option>)

    return <div className="userdataFormFow">
        <label htmlFor={field}>Base education country</label>
        <select id={field} onChange={controller.valueChanges}>
          {result}
        </select>
      </div>
  }
}
