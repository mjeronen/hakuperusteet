export function initEmailAuthentication(hash) {
  const tokenPattern = /^#\/token\/(.*)$/;
  const token = hash.match(tokenPattern)
  if (token != undefined) {
    return { token: token[1], idpentityid: "email" }
  } else {
    return {}
  }
}