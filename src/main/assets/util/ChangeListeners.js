
export function initChangeListeners(dispatcher, events) {

  function valueChanges(fieldName) {
    return (e) => {
      dispatcher.push(events.updateField, { field: fieldName, value: e.target.value })
    }
  }

  function checkedChanges(fieldName) {
    return (e) => {
      dispatcher.push(events.updateField, { field: fieldName, value: e.target.checked })
    }
  }

  return { valueChanges: valueChanges, checkedChanges: checkedChanges }
}

