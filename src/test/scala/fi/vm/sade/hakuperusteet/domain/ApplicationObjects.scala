package fi.vm.sade.hakuperusteet.domain

object ApplicationObjects {

  def generateApplicationObject(u: User) = {
    ApplicationObject(None, u.personOid.get, "1.2.246.562.20.69046715533", "", "102", "008")
  }

}
