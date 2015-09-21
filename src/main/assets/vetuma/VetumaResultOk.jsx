import React from 'react'

import {translation} from '../translations/translations.js'

export default class VetumaResultOk extends React.Component {
  render() {
    return <div className="vetumaResult">
      {translation("vetuma.result.ok")}
    </div>
  }
}