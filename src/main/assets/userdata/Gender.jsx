import React from 'react'
import _ from 'lodash'


export default class Gender extends React.Component {
  componentDidMount() {
    this.props.controller.initFieldValidation("gender", "")
  }

  render() {
    const state = this.props.state
    const controller = this.props.controller
    const name = "gender"
    return <div className="userDataFormRow">
      <label>Gender</label>
      <input type="radio" id="gender-male" name={name} value="1" onChange={controller.radioChanges}/>
      <label htmlFor="gender-male" className="genderLabel">Male</label>
      <input type="radio" id="gender-female" name={name} value="2" onChange={controller.radioChanges}/>
      <label htmlFor="gender-female" className="genderLabel">Female</label>
    </div>
  }
}
