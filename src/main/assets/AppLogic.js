export function sessionInit(state) {
  return !_.isUndefined(state.sessionInit) && state.sessionInit == true
}

export function showLoginInfo(state) {
  return sessionInit(state) && (_.isUndefined(state.sessionData) || _.isUndefined(state.sessionData.session) || _.isUndefined(state.sessionData.session.email))
}

export function hasGoogleSession(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && !_.isUndefined(state.sessionData.session.email) && state.sessionData.session.idpentityid == "google"
}

export function hasEmailSession(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && !_.isUndefined(state.sessionData.session.email) && state.sessionData.session.idpentityid == "oppijaToken"
}

export function hasAuthenticationError(state) {
  return !_.isUndefined(state.authenticationError) && state.authenticationError == true
}

export function showUserDataForm(state) {
  return !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.session) && _.isUndefined(state.sessionData.user)
}

export function showEducationForm(state) {
  return hasUserData(state) && !hasEducationForCurrentHakuOid(state) && !_.isEmpty(state.hakukohdeOid)
}

export function showVetumaStart(state) {
  function hasNoValidPayment() {
    return _.all(state.sessionData.payment, function(p) { return p.status != "ok"})
  }
  return hasUserData(state) && hasEducationForCurrentHakuOid(state) && paymentRequiredWithCurrentHakukohdeOid(state) && hasNoValidPayment()
}

export function showHakuList(state) {
  return hasUserData(state) && (_.isEmpty(state.hakukohdeOid) || (hasEducationForCurrentHakuOid(state) && (hasValidPayment(state) || !paymentRequiredWithCurrentHakukohdeOid(state))))
}

export function hasValidPayment(state) {
  return _.some(state.sessionData.payment, function(p) { return p.status == "ok"})
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

function hasEducationForCurrentHakuOid(state) {
  return !_.isEmpty(state.sessionData.applicationObject) && _.some(state.sessionData.applicationObject, (e) => { return e.hakukohdeOid == state.hakukohdeOid })
}

function paymentRequiredWithCurrentHakukohdeOid(state) {
  const educationForCurrentHakukohdeOid = _.find(state.sessionData.applicationObject, (e) => { return e.hakukohdeOid == state.hakukohdeOid })
  if (_.isEmpty(educationForCurrentHakukohdeOid)) {
    return false
  } else {
    const eeaCountries = (state.properties && state.properties.eeaCountries) ? state.properties.eeaCountries : []
    const isEeaCountry = _.contains(eeaCountries, educationForCurrentHakukohdeOid.educationCountry)
    return !isEeaCountry
  }
}
