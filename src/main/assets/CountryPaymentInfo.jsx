import React from 'react'
import _ from 'lodash'

export default class CountryPaymentInfo extends React.Component {
  render() {
    const country = this.props.country
    const paymentRequired = !_.isUndefined(country) && country !== "Finland" && country !== "Choose.."
    const noPaymentRequired = !_.isUndefined(country) && country === "Finland"
    return <div>
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
