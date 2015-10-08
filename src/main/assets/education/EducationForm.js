import Bacon from 'baconjs'
import moment from 'moment-timezone'

import HttpUtil from '../util/HttpUtil.js'
import {enableSubmitAndHideBusy} from '../util/HtmlUtils.js'

export function submitEducationDataToServer(state) {
  const educationData = {
    hakukohdeOid: state.hakukohdeOid,
    providerOids: state.providerOids.join(" "),
    educationLevel: state.educationLevel,
    educationCountry: state.educationCountry
  }
  const promise = Bacon.fromPromise(HttpUtil.post(state.properties.educationDataUrl, educationData))
  promise.onError((error) => {
    const form = document.getElementById('educationForm')
    enableSubmitAndHideBusy(form)
    form.querySelector("span.general").classList.remove("hide")
  })
  return promise
}