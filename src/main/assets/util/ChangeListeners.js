
export function initChangeListeners(dispatcher, events) {
  function componentOnChangeListener(field, newValue) {
    dispatcher.push(events.updateField, { field: field, value: newValue })
  }

  return { componentOnChangeListener: componentOnChangeListener }
}

