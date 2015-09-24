import Bacon from 'baconjs'
import moment from 'moment-timezone'

import HttpUtil from '../util/HttpUtil.js'
import {enableSubmitAndHideBusy} from '../util/HtmlUtils.js'

export function submitEducationDataToServer(state) {
  const educationData = {
    hakukohdeOid: state.hakukohdeOid,
    educationLevel: state.educationLevel,
    educationCountry: state.educationCountry
  }
  const promise = Bacon.fromPromise(HttpUtil.post(state.properties.educationDataUrl, educationData))
  promise.onError((error) => {
    const form = document.getElementById('educationForm')
    enableSubmitAndHideBusy(form)
    if (error.status == 409) {
      form.querySelector("span.invalid").classList.remove("hide")
    } else {
      form.querySelector("span.general").classList.remove("hide")
    }
  })
  return promise
}