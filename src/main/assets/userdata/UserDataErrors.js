import _ from 'lodash'

export function requiredFieldMissing(state, fieldName) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors[fieldName])
    && _.contains(state.validationErrors[fieldName], "required")
}
