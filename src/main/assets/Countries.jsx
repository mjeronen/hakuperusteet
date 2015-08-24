import React from 'react'
import _ from 'lodash'

export default class Countries extends React.Component {
  constructor(props) {
    super(props)
  }
  render() {
    const lang = this.props.lang.toUpperCase()
    const emptyCountries = [{ id: "", name: "Choose.."}]
    const countries =  _.isUndefined(this.props.countries) ? emptyCountries : this.props.countries

    var parseValues = function (country) {
      const name = country.metadata.filter(function (meta) { return meta.kieli == lang})[0].nimi
      return { id: country.koodiArvo, name: name }
    }
    var sortWith = function(n) { return n.name }
    const result = _.sortBy(countries.map(parseValues), sortWith)

    return <select>
        {result.map(function(country) {
          return <option key={country.id}>{country.name}</option>
        })}
      </select>
  }
}
