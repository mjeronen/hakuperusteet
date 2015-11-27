import _ from 'lodash'

export function tarjontaForHakukohdeOid(state, hakukohdeOid) {
  const emptyTarjonta =  { name : "", description: "", hakuOid: "", baseEducations: [], maksumuuriKaytossa: true, alkuPvm: "", loppuPvm: "" }
  if (_.isUndefined(state.tarjonta)) return emptyTarjonta
  if (_.isUndefined(state.tarjonta[hakukohdeOid])) return emptyTarjonta
  return state.tarjonta[hakukohdeOid]
}

export function getTarjontaNameOrFallback(names, lang){
  if (names[lang]) return names[lang]
  if (names['fi']) return names['fi']
  if (names['sv']) return names['sv']
  if (names['en']) return names['en']
  return names
}