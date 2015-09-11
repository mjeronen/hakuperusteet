import _ from 'lodash'

export function validateField(state, field, value) {
  if (field == "firstName") return validateNonEmptyTextField(value)
  if (field == "lastName") return validateNonEmptyTextField(value)
  if (field == "birthDate") return validateBirthDate(value)
  return []
}

function validateNonEmptyTextField(value) {
  if (_.isEmpty(value)) return ["required"]
  return []
}

function validateBirthDate(value) {
  if (_.isEmpty(value)) return ["required"]
  const eightNumbersPattern = /^([0-9]{8})$/;
  if (eightNumbersPattern.test(value)) {
    return []
  } else {
    return ["invalid"]
  }
}