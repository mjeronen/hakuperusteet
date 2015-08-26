import React from 'react'
import _ from 'lodash'

export default class GoogleAuthentication extends React.Component {
  componentDidMount() {
    gapi.signin2.render('googleAuthentication', {
      'width': 250,
      'height': 50,
      'longtitle': true,
      'theme': 'dark',
      'onsuccess': GoogleAuthentication.onSuccess,
      'onfailure': GoogleAuthentication.onFailure
    })
  }

  render() {
    return <div>
      <div id="googleAuthentication" />
    </div>
  }
}
