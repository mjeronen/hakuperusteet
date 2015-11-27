import React from 'react'

import {translation} from '../../assets-common/translations/translations.js'

export default class VetumaResultOk extends React.Component {
  render() {
    return <div className="vetumaResult ok">
      {translation("vetuma.result.ok")}
    </div>
  }
}