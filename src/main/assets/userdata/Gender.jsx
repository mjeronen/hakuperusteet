import React from 'react'
import _ from 'lodash'

import {translation} from '../../assets-common/translations/translations.js'

export default class Gender extends React.Component {
  constructor(props) {
    super()
    this.id = "gender"
  }

  componentDidMount() {
    if (_.isEmpty(this.props.state[this.id])) this.props.controller.radioChanges({ target: { name: this.id, value: "" }})
    else this.props.controller.radioChanges({ target: { name: this.id, value: this.props.state[this.id] }})
  }

  render() {
    const controller = this.props.controller
    return <div className="userDataFormRow">
      <label>{translation("title.gender.general")  + " *"}</label>
      <input type="radio" id="gender-male" name={this.id} value="1" onChange={controller.radioChanges} checked={this.props.state[this.id] == "1"}/>
      <label htmlFor="gender-male" className="genderLabel">{translation("title.gender.male")}</label>
      <input type="radio" id="gender-female" name={this.id} value="2" onChange={controller.radioChanges} checked={this.props.state[this.id] == "2"}/>
      <label htmlFor="gender-female" className="genderLabel">{translation("title.gender.female")}</label>
    </div>
  }
}
