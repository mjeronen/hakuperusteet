import React from 'react'
import _ from 'lodash'
import {translation} from '../translations/translations.js'

export default class CountryPaymentInfo extends React.Component {
  render() {
    const state = this.props.state
    const eeaCountries = (_.isEmpty(state.properties) || _.isEmpty(state.properties.eeaCountries))
      ? [] : JSON.parse(state.properties.eeaCountries)
    const country = state.educationCountry
    const isEeaCountry = eeaCountries.indexOf(country) !== -1

    const paymentRequired = country && !isEeaCountry
    const noPaymentRequired = country && isEeaCountry
    return <div className="userDataFormRow">
      { paymentRequired
        ? <p>{translation("apply.payment.required")}</p>
        : null
      }
      { noPaymentRequired
        ? <p>{translation("apply.payment.notRequired")}</p>
        : null
      }
      </div>
  }
}
