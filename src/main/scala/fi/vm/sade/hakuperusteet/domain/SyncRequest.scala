package fi.vm.sade.hakuperusteet.domain


trait SyncRequest {

}

case class HakuAppSyncRequest(id: Int, henkiloOid: String, hakemusOid: String) extends SyncRequest
case class ApplicationObjectSyncRequest(id: Int, henkiloOid: String, hakuOid: String, hakukohdeOid: String) extends SyncRequest
