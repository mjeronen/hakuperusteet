import React from 'react'
import _ from 'lodash'

import {googleAuthenticationRenderFailure} from './util/GoogleAuthentication'

export default class GoogleAuthentication extends React.Component {
  componentDidMount() {
    gapi.signin2.render('googleAuthentication', {
      'width': 250,
      'height': 50,
      'longtitle': true,
      'theme': 'dark',
      'onfailure': googleAuthenticationRenderFailure
    })
  }

  render() {
    return <div>
      <div id="googleAuthentication" />
    </div>
  }
}
