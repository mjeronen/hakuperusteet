import _ from 'lodash'

export function requiredField(state, fieldName) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors[fieldName])
    && _.contains(state.validationErrors[fieldName], "required")
}

export function invalidField(state, fieldName) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors[fieldName])
    && _.contains(state.validationErrors[fieldName], "invalid")
}
