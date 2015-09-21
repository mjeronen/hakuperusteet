import React from 'react'
import _ from 'lodash'

import {orderEmailLoginLink} from './EmailAuthentication.js'
import AjaxLoader from '../util/AjaxLoader.jsx'
import {translation} from '../translations/translations.js'

export default class EmailLogIn extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div id="emailAuthentication">
      <p>{translation("login.order.link")}</p>
      <form onSubmit={orderEmailLoginLink(state)} method="POST">
        <label htmlFor="emailToken">{translation("title.email")}</label>
        <input type="text" id="emailToken" name="emailToken" onChange={controller.valueChanges}/>
        <br/>
        <input type="submit" name="submit" value={translation("login.order.button")} />
        <AjaxLoader hide={true} />
        <span className="serverError hide">{translation("errors.server.unexpected")}</span>
        <span className="success hide">{translation("success.login.link")}</span>
      </form>
    </div>
  }
}
