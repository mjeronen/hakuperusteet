import React from 'react'

import {translation} from '../translations/translations.js'

export default class VetumaResultError extends React.Component {
  render() {
    return <div className="vetumaResult">
      {translation("vetuma.result.error")}
    </div>
  }
}