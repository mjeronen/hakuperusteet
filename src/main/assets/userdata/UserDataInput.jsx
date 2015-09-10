import React from 'react'

export default class UserDataInput extends React.Component {
  constructor(props) {
    super()
    this.title = props.title
    this.name = props.name
  }

  componentDidMount() {
    this.props.controller.valueChanges({ target: { id: this.name, value: "" }})
  }

  render() {
    return <div className="userDataFormRow">
        <label htmlFor={this.name}>{this.title}</label>
        <input type="text" id={this.name} name={this.name} onChange={this.props.controller.valueChanges}/>
      </div>
  }
}
