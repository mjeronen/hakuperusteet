import React from 'react'
import _ from 'lodash'
import Flatten from 'flat'

import * as translations from './translations.json'
const flatTrans = Flatten(translations)

export default class LocalizedText extends React.Component {
  render() {
    const lang = "en"
    const translationKey = this.props.translationKey
    const fullKey = translationKey + "." + lang
    const value = _.isEmpty(flatTrans[fullKey]) ? fullKey : flatTrans[fullKey]
    return <span>
      {value}
    </span>
  }
}