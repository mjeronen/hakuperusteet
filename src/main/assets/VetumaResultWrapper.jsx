import React from 'react'
import addons from 'react/addons'
import _ from 'lodash'

var ReactTransitionGroup = React.addons.CSSTransitionGroup

import VetumaResultOk from './VetumaResultOk.jsx'
import VetumaResultCancel from './VetumaResultCancel.jsx'
import VetumaResultError from './VetumaResultError.jsx'

export default class VetumaResultWrapper extends React.Component {
  selectMessage(state) {
    if (!_.isUndefined(state.effect) && state.effect == "#VetumaResultOk") return <VetumaResultOk state={state} />
    if (!_.isUndefined(state.effect) && state.effect == "#VetumaResultCancel") return <VetumaResultCancel state={state} />
    if (!_.isUndefined(state.effect) && state.effect == "#VetumaResultError") return  <VetumaResultError state={state} />
    return null
  }

  render() {
    const state = this.props.state

    return <ReactTransitionGroup transitionName="example" transitionAppear={true}>
      {this.selectMessage(state)}
    </ReactTransitionGroup>
  }
}
