import React from 'react'
import _ from 'lodash'

export default class EmailLogIn extends React.Component {
  render() {
    const controller = this.props.controller

    return <div id="emailAuthentication">
      <p>Order login link to your email.</p>
      <form>
        <label htmlFor="emailToken">Email address</label>
        <input type="text" id="emailToken" name="emailToken" onChange={controller.valueChanges}/>
        <input type="submit" name="submit" value="Order email login token" />
        <img className="ajax-loader hide" src="/hakuperusteet/img/ajax-loader.gif" />
      </form>
    </div>
  }
}
