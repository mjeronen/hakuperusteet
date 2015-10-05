import React from 'react'

export default class UserDataInput extends React.Component {
  constructor(props) {
    super()
    this.title = props.title
    this.name = props.name
    this.changes = props.controller.valueChanges
  }

  componentDidMount() {
    this.changes({ target: { id: this.name, value: "" }})
  }

  render() {
    return <div className="userDataFormRow">
        <label htmlFor={this.name}>{this.title}</label>
        <input type="text" id={this.name} name={this.name} onChange={this.changes} onBlur={this.changes} maxLength="255" />
      </div>
  }
}
