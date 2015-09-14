import Bacon from 'baconjs'
import moment from 'moment-timezone'

import HttpUtil from './HttpUtil.js'
import {enableSubmitAndHideBusyAndShowError} from './HtmlUtils.js'

export function submitUserDataToServer(state) {
  const userData = {
    firstName: state.firstName,
    lastName: state.lastName,
    birthDate: moment(state.birthDate, "DDMMYYYY").tz('Europe/Helsinki').format("YYYY-MM-DD"),
    personId: state.personId,
    gender: state.gender,
    nativeLanguage: state.nativeLanguage,
    nationality: state.nationality,
    educationLevel: state.educationLevel,
    educationCountry: state.educationCountry
  }
  const promise = Bacon.fromPromise(HttpUtil.post(state.properties.userDataUrl, userData))
  promise.onError((_) => {
    const form = document.getElementById('userDataForm')
    enableSubmitAndHideBusyAndShowError(form)
  })
  return promise
}