import _ from 'lodash'

export function validateField(state, field, value) {
  if (field == "firstName") return validateNonEmptyTextField(value)
  if (field == "lastName") return validateNonEmptyTextField(value)
  return []
}

function validateNonEmptyTextField(value) {
  if (_.isEmpty(value)) return ["required"]
  return []
}