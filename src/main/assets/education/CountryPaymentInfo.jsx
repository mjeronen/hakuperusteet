import React from 'react'
import _ from 'lodash'
import {translation} from '../../assets-common/translations/translations.js'

export default class CountryPaymentInfo extends React.Component {
  render() {
    const state = this.props.state
    const eeaCountries = (_.isEmpty(state.properties) || _.isEmpty(state.properties.eeaCountries)) ? [] : state.properties.eeaCountries
    const country = state.educationCountry
    const isEeaCountry = eeaCountries.indexOf(country) !== -1

    const paymentRequired = country && !isEeaCountry
    const noPaymentRequired = country && isEeaCountry
    return <div className="userDataFormRow">
      { paymentRequired
        ? <p className="paymentRequired">{translation("apply.payment.required")}</p>
        : null
      }
      { noPaymentRequired
        ? <p className="noPaymentRequired">{translation("apply.payment.notRequired")}</p>
        : null
      }
      </div>
  }
}
