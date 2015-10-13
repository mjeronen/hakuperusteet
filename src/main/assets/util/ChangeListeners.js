import {disableSubmitAndShowBusy} from './HtmlUtils.js'

export function initChangeListeners(dispatcher, events) {
  function valueChanges(e) {
    const field = e.target.id
    const value = e.target.value
    pushChangeAndValidation(field, value)
  }

  function checkedChanges(e) {
    const field = e.target.id
    const value = e.target.checked
    pushChangeAndValidation(field, value)
  }

  function radioChanges(e) {
    const field = e.target.name
    const value = e.target.value
    pushChangeAndValidation(field, value)
  }

  function formSubmits(e) {
    e.preventDefault()
    const form = document.getElementById(e.target.id)
    disableSubmitAndShowBusy(form)
    dispatcher.push(events.submitForm, e.target.id)
  }
  function pushRouteChange(path) {
    dispatcher.push(events.route, path)
  }
  function pushEducationFormChanges(ao, e) {
    const field = e.target.id
    const value = e.target.value
    dispatcher.push(events.updateEducationForm, {...ao, [field]: value})
  }
  function pushChangeAndValidation(field, value) {
    dispatcher.push(events.updateField, {field: field, value: value})
    dispatcher.push(events.fieldValidation, {field: field, value: value})
  }

  function logOut() {
    dispatcher.push(events.logOut, {})
  }
  return { pushEducationFormChanges: pushEducationFormChanges, pushChangeAndValidation: pushChangeAndValidation, pushRouteChange: pushRouteChange, valueChanges: valueChanges, checkedChanges: checkedChanges, radioChanges: radioChanges, formSubmits: formSubmits, logOut: logOut }
}

