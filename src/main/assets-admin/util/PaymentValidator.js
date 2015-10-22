import _ from 'lodash'

const selectFields = ["status"]

export function paymentWithValidationErrors(payment) {
  const currentValidationErrors = payment.validationErrors || {}
  const newValidationErrors = _.reduce(selectFields, (result, field) => ({...result, [field]: validateSelect(payment[field])}), {})
  return {...payment, ['validationErrors']: {...currentValidationErrors, ...newValidationErrors}}
}

function validateSelect(value) {
  return (_.isEmpty(value)) ? ["required"] : []
}

export function validatePayment(payment) {
  return !_.isEmpty(payment.status)
}
