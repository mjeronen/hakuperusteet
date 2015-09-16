import Bacon from 'baconjs'

import HttpUtil from '../util/HttpUtil'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusy, enableSubmitAndHideBusyAndShowError} from '../util/HtmlUtils.js'

export function isLoginToken(hash) {
  return hash.startsWith("#/token/")
}

export function initEmailAuthentication(hash) {
  const tokenPattern = /^#\/token\/(.*)$/;
  const token = hash.match(tokenPattern)
  if (token != undefined) {
    return { token: token[1], idpentityid: "oppijaToken" }
  } else {
    return {}
  }
}

export function orderEmailLoginLink(state) {
  return (e) => {
    e.preventDefault()
    const form = e.target
    disableSubmitAndShowBusy(form)
    const promise = Bacon.fromPromise(HttpUtil.post(state.properties.emailTokenUrl, {email: state.emailToken}))
    promise.onValue((result) => {
      enableSubmitAndHideBusy(form)
      form.querySelector(".success").classList.remove("hide")
    })
    promise.onError((_) => { enableSubmitAndHideBusyAndShowError(form) })
  }
}
