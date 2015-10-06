import _ from 'lodash'

export function requiredPersonIdMissing(state) {
  return !_.isEmpty(state.validationErrors) && !_.isEmpty(state.validationErrors.personId)
    && _.contains(state.validationErrors.personId, "required")
}