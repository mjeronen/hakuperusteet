import _ from 'lodash'

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

export function validateEmailForm(state) {
  return !_.isEmpty(state.emailToken) && _.contains(state.emailToken, "@")
    && !_.contains(state.emailToken, " ") && !_.contains(state.emailToken, ",") && !_.contains(state.emailToken, "\t")
}

export function validateUserDataForm(state) {
  const allV = state.validationErrors || {}
  const userV = [allV.firstName, allV.lastName, allV.birthDate, allV.personId, allV.gender, allV.nativeLanguage,
    allV.nationality].filter(function(x) {return !_.isEmpty(x) })
  return _.all(userV, function(v) { return v.length == 0})
}

export function validateEducationForm(state) {
  const allV = state.validationErrors || {}
  const userV = [allV.educationLevel, allV.educationCountry].filter(function(x) {return !_.isEmpty(x) })
  return _.all(userV, function(v) { return v.length == 0})
}

function validatePersonId(hasPersonId, value) {
  if (hasPersonId == true) {
    return (value.length == 5) ? [] : ["required"]
  } else {
    return []
  }
}

function validateField(field, value) {
  if (field == "firstName") return validateNonEmptyTextField(value).concat(validateNameField(value))
  if (field == "lastName") return validateNonEmptyTextField(value).concat(validateNameField(value))
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

function validateNameField(value) {
  const latin1Subset = /^$|^[a-zA-ZÀ-ÖØ-öø-ÿ]$|^[a-zA-ZÀ-ÖØ-öø-ÿ'][a-zA-ZÀ-ÖØ-öø-ÿ ,-.']*(?:[a-zA-ZÀ-ÖØ-öø-ÿ.']+$)$/;
  return latin1Subset.test(value) ? [] : ["invalid"]
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
  return (_.isEmpty(value)) ? ["required"] : []
}

export function requiredField(state, fieldName) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors[fieldName])
    && _.contains(state.validationErrors[fieldName], "required")
}

export function invalidField(state, fieldName) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors[fieldName])
    && _.contains(state.validationErrors[fieldName], "invalid")
}
