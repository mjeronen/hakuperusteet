import React from 'react'

import {translation} from '../../assets-common/translations/translations.js'

export default class VetumaResultCancel extends React.Component {
  render() {
    return <div className="vetumaResult fail">
      {translation("vetuma.result.cancel")}
    </div>
  }
}