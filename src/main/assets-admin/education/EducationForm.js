import Bacon from 'baconjs'

import HttpUtil from '../../assets/util/HttpUtil.js'
import {enableSubmitAndHideBusy} from '../../assets/util/HtmlUtils.js'
import {tarjontaForHakukohdeOid} from "../../assets/util/TarjontaUtil.js"

export function submitEducationDataToServer(state, ao, form) {
  const educationData = {
    id: ao.id,
    personOid: state.personOid,
    hakuOid: tarjontaForHakukohdeOid(state, state.hakukohdeOid).hakuOid,
    hakukohdeOid: ao.hakukohdeOid,
    educationLevel: ao.educationLevel,
    educationCountry: ao.educationCountry
  }
  const promise = Bacon.fromPromise(HttpUtil.post(state.applicationObjectUpdateUrl, educationData))
  promise.onError((error) => {
    enableSubmitAndHideBusy(form)
    form.querySelector("span.general").classList.remove("hide")
  })
  return promise
}