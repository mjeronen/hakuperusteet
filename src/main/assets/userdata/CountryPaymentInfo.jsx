import React from 'react'
import _ from 'lodash'

export default class CountryPaymentInfo extends React.Component {
  render() {
    const state = this.props.state
    const eeaCountries = state.eeaCountries
    const country = state.educationCountry
    const isEeaCountry = eeaCountries.indexOf(country) !== -1

    const paymentRequired = country && !isEeaCountry
    const noPaymentRequired = country && isEeaCountry
    return <div className="userDataFormRow">
      { paymentRequired
        ? <p>This Master's Program has applying charge of 100 €.</p>
        : null
      }
      { noPaymentRequired
        ? <p>With EEA-based education you don't need to pay applying charge.</p>
        : null
      }
      </div>
  }
}
