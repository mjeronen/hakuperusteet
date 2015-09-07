export function showUserDataForm(state) {
  return !_.isUndefined(state.sessionData) && _.isUndefined(state.sessionData.user)
}

export function showVetumaStart(state) {
  function hasNoValidPayment() {
    return _.all(state.sessionData.payment, function(p) { return p.status != "ok"})
  }
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && hasNoValidPayment()
}

export function showHakuList(state) {
  function hasValidPayment() {
    return _.some(state.sessionData.payment, function(p) { return p.status == "ok"})
  }
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && hasValidPayment()
}

export function showVetumaResultOk(state) {
  return !_.isUndefined(state.effect) && state.effect == "#VetumaResultOk"
}

export function showVetumaResultCancel(state) {
  return !_.isUndefined(state.effect) && state.effect == "#VetumaResultCancel"
}
export function showVetumaResultError(state) {
  return !_.isUndefined(state.effect) && state.effect == "#VetumaResultError"
}

