import React from 'react'
import addons from 'react/addons'
import _ from 'lodash'

var ReactTransitionGroup = React.addons.CSSTransitionGroup

import {showVetumaResultOk, showVetumaResultCancel, showVetumaResultError} from '../AppLogic.js'

import VetumaResultOk from './VetumaResultOk.jsx'
import VetumaResultCancel from './VetumaResultCancel.jsx'
import VetumaResultError from './VetumaResultError.jsx'

export default class VetumaResultWrapper extends React.Component {
  selectMessage(state) {
    if (showVetumaResultOk(state)) return <VetumaResultOk state={state} />
    if (showVetumaResultCancel(state)) return <VetumaResultCancel state={state} />
    if (showVetumaResultError(state)) return  <VetumaResultError state={state} />
    return null
  }
  render() {
    const state = this.props.state
    return <ReactTransitionGroup transitionName="vetuma-result" transitionAppear={true}>
      {this.selectMessage(state)}
    </ReactTransitionGroup>
  }
}
