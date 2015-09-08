import React from 'react'
import Bacon from 'baconjs'
import _ from 'lodash'

import style from './css/hakuperusteet.less'

import {showLoginInfo, showUserDataForm, showVetumaStart, showHakuList} from './AppLogic.js'
import Header from './Header.jsx'
import LoginInfo from './LoginInfo.jsx'
import ProgramInfo from './ProgramInfo.jsx'
import Footer from './Footer.jsx'
import GoogleAuthentication from './GoogleAuthentication.jsx'
import VetumaResultWrapper from './vetuma/VetumaResultWrapper.jsx'
import UserDataForm from './userdata/UserDataForm.jsx'
import VetumaStart from './vetuma/VetumaStart.jsx'
import HakuList from './HakuList.jsx'

export default class HakuperusteetPage extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      <Header />
      <GoogleAuthentication state={state} />
      { showLoginInfo(state) ? <LoginInfo state={state} controller={controller}/> : null}
      { showUserDataForm(state) ? <UserDataForm state={state} controller={controller}/> : null}
      { showVetumaStart(state) ? <VetumaStart state={state} /> : null}
      { showHakuList(state) ? <HakuList state={state} /> : null}
      <VetumaResultWrapper state={state}/>
      <ProgramInfo state={state} />
      <Footer />
    </div>
  }
}
