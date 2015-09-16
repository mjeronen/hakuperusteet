export function showLoginInfo(state) {
  return _.isUndefined(state.sessionData) || _.isUndefined(state.sessionData.session) || _.isUndefined(state.sessionData.session.email)
}

export function hasGoogleSession(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && !_.isUndefined(state.sessionData.session.email) && state.sessionData.session.idpentityid == "google"
}

export function hasEmailSession(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && !_.isUndefined(state.sessionData.session.email) && state.sessionData.session.idpentityid == "oppijaToken"
}

export function showUserDataForm(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && _.isUndefined(state.sessionData.user)
}

export function showVetumaStart(state) {
  function paymentRequired() {
    return state.sessionData.shouldPay === true
  }
  function hasNoValidPayment() {
    return _.all(state.sessionData.payment, function(p) { return p.status != "ok"})
  }
  return hasUserData(state) && paymentRequired() && hasNoValidPayment()
}

export function showHakuList(state) {
  function noPaymentRequired() {
    return state.sessionData.shouldPay === false
  }
  function hasValidPayment() {
    return _.some(state.sessionData.payment, function(p) { return p.status == "ok"})
  }
  return hasUserData(state) && (hasValidPayment() || noPaymentRequired())
}

export function showVetumaResultOk(state) {
  return !_.isUndefined(state.effect) && state.effect == "VetumaResultOk"
}

export function showVetumaResultCancel(state) {
  return !_.isUndefined(state.effect) && state.effect == "VetumaResultCancel"
}
export function showVetumaResultError(state) {
  return !_.isUndefined(state.effect) && state.effect == "VetumaResultError"
}

function hasUserData(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user)
}
