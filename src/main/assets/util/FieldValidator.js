import _ from 'lodash'

import {emptySelectValue} from './HtmlUtils.js'

const personIdFields = ["personId", "hasPersonId"]
const selectFields = ["educationCountry", "educationLevel", "nationality", "nativeLanguage"]

export function parseNewValidationErrors(state, field, value) {
  const currentValidationErrors = state.validationErrors || {}
  if (_.contains(personIdFields, field)) {
    return {...currentValidationErrors, ["personId"]: validatePersonId(state.hasPersonId, value) }
  } else {
    return {...currentValidationErrors, [field]: validateField(field, value) }
  }
}

function validatePersonId(hasPersonId, value) {
  if (hasPersonId == true) {
    return (value.length == 5) ? [] : ["required"]
  } else {
    return []
  }
}

function validateField(field, value) {
  if (field == "firstName") return validateNonEmptyTextField(value)
  if (field == "lastName") return validateNonEmptyTextField(value)
  if (field == "birthDate") return validateBirthDate(value)
  if (field == "personId") return validatePersonId(value)
  if (field == "gender") return validateGender(value)
  if (_.contains(selectFields, field)) {
    return validateSelect(value)
  }

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
function validateGender(value) {
  return (value.length == 0) ? ["required"] : []
}

function validateSelect(value) {
  return (value == emptySelectValue()) ? ["required"] : []
}