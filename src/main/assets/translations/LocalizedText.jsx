import React from 'react'
import _ from 'lodash'
import Flatten from 'flat'

import * as translations from './translations.json'
const flatTrans = Flatten(translations)

export default class LocalizedText extends React.Component {
  trans(fullKey) {
    if(_.isEmpty(flatTrans[fullKey])) {
      console.log("Missing key " + fullKey)
      return fullKey
    } else {
      return flatTrans[fullKey]
    }
  }

  render() {
    const lang = "en"
    const translationKey = this.props.translationKey
    const fullKey = translationKey + "." + lang
    return <span>
      {this.trans(fullKey)}
    </span>
  }
}