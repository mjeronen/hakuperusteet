import React from 'react'

export function emptySelectValue() {
  return "Choose..."
}

export function createSelectOptions(data) {
  const emptyOptions = [{ id: "", name: emptySelectValue()}]
  const dataJson = data ? JSON.parse(data) : emptyOptions
  var toOptions = function (item) { return <option value={item.id} key={item.id}>{item.name}</option> }
  const result = dataJson.map(toOptions)
  result.unshift(<option value="" key="-">{emptySelectValue()}</option>)
  return result
}