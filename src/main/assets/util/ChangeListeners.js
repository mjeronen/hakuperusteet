
export function initChangeListeners(dispatcher, events) {

  function valueChanges(e) {
    dispatcher.push(events.updateField, { field: e.target.id, value: e.target.value })
  }

  function checkedChanges(e) {
    dispatcher.push(events.updateField, { field: e.target.id, value: e.target.checked })
  }

  function radioChanges(e) {
    dispatcher.push(events.updateField, { field: e.target.name, value: e.target.value })
  }

  function formSubmits(e) {
    e.preventDefault()
    dispatcher.push(events.submitForm, e.target.id)
  }

  return { valueChanges: valueChanges, checkedChanges: checkedChanges, radioChanges: radioChanges, formSubmits: formSubmits }
}

