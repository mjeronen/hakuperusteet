import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import Header from './Header.jsx'
import ProgramInfo from './ProgramInfo.jsx'
import Footer from './Footer.jsx'
import GoogleAuthentication from './GoogleAuthentication.jsx'
import UserDataForm from './UserDataForm.jsx'
import VetumaStart from './VetumaStart.jsx'
import HakuList from './HakuList.jsx'

export default class HakuperusteetPage extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      <Header />
      <GoogleAuthentication state={state} />
      <ProgramInfo state={state} />
      { !_.isUndefined(state.sessionData) && _.isUndefined(state.sessionData.user)
        ? <UserDataForm state={state} controller={controller}/>
        : null
      }
      { !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && (_.isUndefined(state.sessionData.payment) || (state.sessionData.payment.status != "ok"))
        ? <VetumaStart state={state} />
        : null
      }
      { !_.isUndefined(state.sessionData) && !_.isUndefined(state.sessionData.user) && !_.isUndefined(state.sessionData.payment) && (state.sessionData.payment.status == "ok")
        ? <HakuList state={state} />
        : null
      }
      <Footer />
    </div>
  }
}
