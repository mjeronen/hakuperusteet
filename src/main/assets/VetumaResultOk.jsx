import React from 'react'
import addons from 'react/addons'
var ReactTransitionGroup = React.addons.CSSTransitionGroup;

export default class VetumaResultOk extends React.Component {
  render() {
    return <ReactTransitionGroup transitionName="example" transitionAppear={true}>
      <div>
        <p>
          Payment successful.
        </p>
      </div>
    </ReactTransitionGroup>
  }
}