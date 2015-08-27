
export function initChangeListeners(dispatcher, events) {

  function valueChanges(e) {
    dispatcher.push(events.updateField, { field: e.target.id, value: e.target.value })
  }

  function checkedChanges(e) {
    dispatcher.push(events.updateField, { field: e.target.id, value: e.target.checked })
  }

  return { valueChanges: valueChanges, checkedChanges: checkedChanges }
}

