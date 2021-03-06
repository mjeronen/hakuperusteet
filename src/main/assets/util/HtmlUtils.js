import React from 'react'
import _ from 'lodash'

export function createSelectOptions(data) {
  const emptyOptions = [{ id: "", name: "Loading..."}]
  const dataJson = _.isEmpty(data) ? emptyOptions : data
  var toOptions = function (item) { return <option value={item.id} key={item.id}>{item.name}</option> }
  const result = dataJson.map(toOptions)
  result.unshift(<option value="" key="-">{"Choose..."}</option>)
  return result
}

export function disableSubmitAndShowBusy(form) {
  form.querySelector("input[type=submit]").setAttribute("disabled", "disabled");
  form.querySelector(".ajax-loader").classList.remove("hide")
  form.querySelector(".serverError").classList.add("hide")
}

export function enableSubmitAndHideBusyAndShowError(form) {
  enableSubmitAndHideBusy(form)
  form.querySelector(".serverError").classList.remove("hide")
}

export function enableSubmitAndHideBusy(form) {
  form.querySelector("input[type=submit]").removeAttribute("disabled");
  form.querySelector(".ajax-loader").classList.add("hide")
}