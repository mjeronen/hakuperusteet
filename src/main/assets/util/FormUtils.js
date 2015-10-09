import Bacon from 'baconjs'
import _ from 'lodash'

import HttpUtil from '../util/HttpUtil'
import {disableSubmitAndShowBusy, enableSubmitAndHideBusyAndShowError} from '../util/HtmlUtils.js'
import {translation} from '../../assets-common/translations/translations.js'

export function fetchUrlParamsAndRedirectPost(url) {
  return (e) => {
    e.preventDefault()
    const form = e.target
    disableSubmitAndShowBusy(form)
    const promise = Bacon.fromPromise(HttpUtil.get(url))
    promise.onValue((result) => {
      form.action = result.url
      for (let p in result.params) {
        form.appendChild(addHidden(p, result.params[p]))
      }
      form.submit()
    })
    promise.onError((_) => { enableSubmitAndHideBusyAndShowError(form) })
  }
}

function addHidden(key, value) {
  const input = document.createElement('input')
  input.type = 'hidden'
  input.name = key
  input.value = value
  return input
}