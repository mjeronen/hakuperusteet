package fi.vm.sade.hakuperusteet.domain

import java.util.Date

import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus

case class Payment(personOid: String, timestamp: Date, reference: String, orderNumber: String, status: PaymentStatus)

object PaymentStatus extends Enumeration {
  type PaymentStatus = Value
  val started, ok, cancel, error = Value
}
