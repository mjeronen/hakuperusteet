import _ from 'lodash'

const selectFields = ["status"]

export function parseNewPaymentValidationErrors(state) {
  return _.reduce(selectFields, (result, field) => ({...result, [field]: validateSelect(state[field])}), {})
}

function validateSelect(value) {
  return (_.isEmpty(value)) ? ["required"] : []
}

export function validatePayment(payment) {
  return !_.isEmpty(payment.status)
}
