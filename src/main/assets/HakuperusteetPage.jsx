import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Header from './Header.jsx'
import Footer from './Footer.jsx'
import GoogleAuthentication from './GoogleAuthentication.jsx'
import UserDataForm from './UserDataForm.jsx'
import VetumaStart from './VetumaStart.jsx'

export default class HakuperusteetPage extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      <Header />
      <div>Hakuperusteet main page</div>
      <GoogleAuthentication state={state} />
      { _.isUndefined(state.henkiloOid)
        ? <UserDataForm state={state} controller={controller}/>
        : null
      }
      { (!_.isUndefined(state.henkiloOid) && _.isUndefined(state.paymentDone))
        ? <VetumaStart state={state} />
        : null
      }
      <Footer />
    </div>
  }
}
