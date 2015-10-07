import Bacon from "baconjs"
//import Http from "./http"

const b = new Bacon.Bus()

const navigate = function (path) {
  history.pushState(null, null, "/hakuperusteetadmin" + path)
  b.push(path)
}

export const routeP = b.toProperty(document.location.pathname)

export const navigateToUser = user => navigate(`/oppija/${user.personOid}`)