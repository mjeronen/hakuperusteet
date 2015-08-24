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
    return <select>
        {countries.map(function(country) {
          const name = country.metadata.filter(function(meta) {return meta.kieli == lang})[0].nimi
          return <option key={country.koodiArvo}>{name}</option>
        })}
      </select>
  }
}
