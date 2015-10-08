package fi.vm.sade.hakuperusteet.domain

case class ApplicationObject(id: Option[Int], personOid: String, hakukohdeOid: String, formId: String, educationLevel: String, educationCountry: String)
