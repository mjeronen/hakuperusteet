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
    form.querySelector("input[type=submit]").setAttribute("disabled", "disabled")
    form.querySelector(".ajax-loader").className = "ajax-loader"
    dispatcher.push(events.submitForm, e.target.id)
  }

  function pushChangeAndValidation(field, value) {
    dispatcher.push(events.updateField, {field: field, value: value})
    dispatcher.push(events.fieldValidation, {field: field, value: value})
  }

  return { valueChanges: valueChanges, checkedChanges: checkedChanges, radioChanges: radioChanges, formSubmits: formSubmits }
}

