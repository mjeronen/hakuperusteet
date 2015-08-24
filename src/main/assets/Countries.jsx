import React from 'react'
import _ from 'lodash'

export default class Countries extends React.Component {
  constructor(props) {
    super(props)
  }
  render() {
    const emptyCountries = [{ id: "", name: "Choose.."}]
    const countries =  _.isUndefined(this.props.countries) ? emptyCountries : this.props.countries
    return <select>
        {countries.map(function(country) {
          return <option key={country.id}>{country.name}</option>
        })}
      </select>
  }
}
