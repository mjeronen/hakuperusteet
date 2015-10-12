import React from 'react'
import _ from 'lodash'

import {translation} from '../../assets-common/translations/translations.js'

export default class EmptyProgramInfo extends React.Component {
  render() {
    return <div>
      <h1>{translation("programinfo.empty.title")}</h1>
      <p>{translation("programinfo.empty.text")}</p>
    </div>
  }
}