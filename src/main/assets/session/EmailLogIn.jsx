import React from 'react'
import _ from 'lodash'

import {orderEmailLoginLink} from './EmailAuthentication.js'
import AjaxLoader from '../util/AjaxLoader.jsx'

export default class EmailLogIn extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div id="emailAuthentication">
      <p>Order login link to your email.</p>
      <form onSubmit={orderEmailLoginLink(state)} method="POST">
        <label htmlFor="emailToken">Email address</label>
        <input type="text" id="emailToken" name="emailToken" onChange={controller.valueChanges}/>
        <br/>
        <input type="submit" name="submit" value="Order email login token" />
        <AjaxLoader hide={true} />
        <span className="serverError hide">Unexpected server error. Please try again later.</span>
        <span className="success hide">Login link sent to your email.</span>
      </form>
    </div>
  }
}
