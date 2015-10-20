import Bacon from 'baconjs'

import HttpUtil from '../../assets/util/HttpUtil.js'
import {enableSubmitAndHideBusy} from '../../assets/util/HtmlUtils.js'

export function submitUserDataToServer(state) {
    const userData = {
        id: state.id,
        email: state.email,
        firstName: state.firstName,
        lastName: state.lastName,
        birthDate: state.birthDate,
        personOid: state.personOid,
        gender: state.gender,
        nativeLanguage: state.nativeLanguage,
        nationality: state.nationality,
        idpentityid: state.idpentityid
    }

    if (!_.isEmpty(state.personId) && state.hasPersonId) {
        userData.personId = state.personId
    }

    const promise = Bacon.fromPromise(HttpUtil.post(state.userUpdateUrl, userData))
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