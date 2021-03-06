import React from 'react'
import _ from 'lodash'

import './css/props.less'
import '../assets-common/css/hakuperusteet.less'

import {showUserDataForm, showEducationForm, showVetumaStart, showHakuList} from './AppLogic.js'
import Header from './Header.jsx'
import Session from './session/Session.jsx'
import ProgramInfo from './programinfo/ProgramInfo.jsx'
import Footer from '../assets-common/Footer.jsx'
import VetumaResultWrapper from './vetuma/VetumaResultWrapper.jsx'
import UserDataForm from './userdata/UserDataForm.jsx'
import EducationForm from './education/EducationForm.jsx'
import VetumaStart from './vetuma/VetumaStart.jsx'
import HakuList from './hakulist/HakuList.jsx'

export default class HakuperusteetPage extends React.Component {
  render() {
    const state = this.props.state
    const controller = this.props.controller
    return <div>
      <Header />
      <ProgramInfo state={state} />
      <Session state={state} controller={controller} />
      { showUserDataForm(state) ? <UserDataForm state={state} controller={controller} /> : null}
      { showEducationForm(state) ? <EducationForm state={state} controller={controller} /> : null}
      { showVetumaStart(state) ? <VetumaStart state={state} /> : null}
      { showHakuList(state) ? <HakuList state={state} /> : null}
      <VetumaResultWrapper state={state}/>
      <Footer />
    </div>
  }
}
