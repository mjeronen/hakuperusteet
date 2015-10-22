import _ from 'lodash'

const selectFields = ["educationCountry", "educationLevel"]

export function applicationObjectWithValidationErrors(ao) {
  const currentValidationErrors = ao.validationErrors || {}
  const newValidationErrors = _.reduce(selectFields, (result, field) => ({...result, [field]: validateSelect(ao[field])}), {})
  return {...ao, ['validationErrors']: {...currentValidationErrors, ...newValidationErrors}}
}

function validateSelect(value) {
  return (_.isEmpty(value)) ? ["required"] : []
}

export function validateApplicationObject(ao) {
  return !_.isEmpty(ao.educationLevel) && !_.isEmpty(ao.educationCountry)
}
