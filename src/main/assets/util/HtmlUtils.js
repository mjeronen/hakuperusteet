import React from 'react'

export function emptySelectValue() {
  return "Choose..."
}

export function createSelectOptions(data, optionalFilter) {
  const filter = optionalFilter ? optionalFilter : function() {return true}
  const emptyOptions = [{ id: "", name: emptySelectValue()}]
  const dataJson = data ? JSON.parse(data) : emptyOptions
  var toOptions = function (item) { return <option value={item.id} key={item.id}>{item.name}</option> }
  const result = dataJson.filter(filter).map(toOptions)
  result.unshift(<option value="" key="-">{emptySelectValue()}</option>)
  return result
}