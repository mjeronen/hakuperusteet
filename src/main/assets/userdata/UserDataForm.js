import Bacon from 'baconjs'
import moment from 'moment-timezone'

import HttpUtil from '../util/HttpUtil.js'
import {enableSubmitAndHideBusy} from '../util/HtmlUtils.js'

export function submitUserDataToServer(state) {
  const userData = {
    firstName: state.firstName,
    lastName: state.lastName,
    birthDate: moment(state.birthDate, "DDMMYYYY").tz('Europe/Helsinki').format("YYYY-MM-DD"),
    personId: state.personId,
    gender: state.gender,
    nativeLanguage: state.nativeLanguage,
    nationality: state.nationality,
  }
  const promise = Bacon.fromPromise(HttpUtil.post(state.properties.userDataUrl, userData))
  promise.onError((error) => {
    const form = document.getElementById('userDataForm')
    enableSubmitAndHideBusy(form)
    if (error.status == 409) {
      form.querySelector("span.invalid").classList.remove("hide")
    } else {
      form.querySelector("span.general").classList.remove("hide")
    }
  })
  return promise
}