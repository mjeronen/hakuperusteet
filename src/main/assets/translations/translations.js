import _ from 'lodash'
import Flatten from 'flat'

import * as translations from './translations.json'
const flatTrans = Flatten(translations)

export function translation(key) {
  const lang = "en"
  const fullKey = key + "." + lang
  const trans = flatTrans[fullKey]
  if(_.isEmpty(trans)) {
    console.log("Missing key " + fullKey)
    return fullKey
  } else {
    return trans
  }

}
