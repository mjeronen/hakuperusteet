import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from './js/HttpUtil.js'

export default class Countries extends React.Component {
  constructor(props) {
    super(props)
  }
  render() {
    const emptyCountries = [{ id: "", name: "Choose.."}]
    const countries =  _.isUndefined(this.props.countries) ? emptyCountries : this.props.countries
    console.log(countries)

    return <select>
        {countries.map(function(country) {
          return <option key={country.id}>{country.name}</option>
        })}
      </select>
  }
}
