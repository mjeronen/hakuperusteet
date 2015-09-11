import _ from 'lodash'

export function validateField(state, field, value) {
  if (field == "firstName") return validateNonEmptyTextField(value)
  if (field == "lastName") return validateNonEmptyTextField(value)
  if (field == "birthDate") return validateBirthDate(value)
  if (field == "personId") return validatePersonId(value)
  return []
}

function validateNonEmptyTextField(value) {
  return (_.isEmpty(value)) ? ["required"] : []
}

function validateBirthDate(value) {
  const eightNumbersPattern = /^([0-9]{8})$/;
  if (eightNumbersPattern.test(value)) {
    return []
  } else {
    return ["invalid"]
  }
}

function validatePersonId(value) {
  return (value.length == 5) ? [] : ["required"]
}
