import _ from 'lodash'

const selectFields = ["educationCountry", "educationLevel"]

export function parseNewApplicationObjectValidationErrors(state) {
  return _.reduce(selectFields, (result, field) => ({...result, [field]: validateSelect(state[field])}), {})
}

function validateSelect(value) {
  return (_.isEmpty(value)) ? ["required"] : []
}

export function validateApplicationObject(ao) {
  return !_.isEmpty(ao.educationLevel) && !_.isEmpty(ao.educationCountry)
}
