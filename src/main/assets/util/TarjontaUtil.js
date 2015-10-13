import _ from 'lodash'

export function tarjontaForHakukohdeOid(state, hakukohdeOid) {
  const emptyTarjonta =  {name : "", description: "", hakuOid: "", baseEducations: []}
  if (_.isUndefined(state.tarjonta)) return emptyTarjonta
  if (_.isUndefined(state.tarjonta[hakukohdeOid])) return emptyTarjonta
  return state.tarjonta[hakukohdeOid]
}