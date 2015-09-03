import React from 'react'
import addons from 'react/addons'
import _ from 'lodash'

var ReactTransitionGroup = React.addons.CSSTransitionGroup

import VetumaResultOk from './VetumaResultOk.jsx'
import VetumaResultCancel from './VetumaResultCancel.jsx'
import VetumaResultError from './VetumaResultError.jsx'

export default class VetumaResultWrapper extends React.Component {
  render() {
    const state = this.props.state
    const showOk = !_.isUndefined(state.effect) && state.effect == "#VetumaResultOk"
    const showCancel = !_.isUndefined(state.effect) && state.effect == "#VetumaResultCancel"
    const showError = !_.isUndefined(state.effect) && state.effect == "#VetumaResultError"

    var result = null
    if (showOk) result = <VetumaResultOk state={state} />
    if (showCancel) result = <VetumaResultCancel state={state} />
    if (showError) result = <VetumaResultError state={state} />

    return <ReactTransitionGroup transitionName="example" transitionAppear={true}>
      {result}
    </ReactTransitionGroup>
  }
}
