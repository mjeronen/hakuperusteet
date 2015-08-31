import React from 'react'
import _ from 'lodash'


export default class Gender extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    const name = "gender"
    return <div>
      <label>Gender</label>
      &nbsp;&nbsp;
      <label htmlFor="gender-male">Male</label>
      <input type="radio" id="gender-male" name={name} value="0" onChange={controller.radioChanges}/>
      <label htmlFor="gender-female">Female</label>
      <input type="radio" id="gender-female" name={name} value="1" onChange={controller.radioChanges}/>
    </div>
  }
}
