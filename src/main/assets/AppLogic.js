export function showUserDataForm(state) {
  return !_.isUndefined(state.sessionData) && _.isUndefined(state.sessionData.user)
}

export function showVetumaStart(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && (_.isUndefined(state.sessionData.payment) || (state.sessionData.payment.status != "ok"))
}

export function showHakuList(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && !_.isUndefined(state.sessionData.payment) && (state.sessionData.payment.status == "ok")
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

