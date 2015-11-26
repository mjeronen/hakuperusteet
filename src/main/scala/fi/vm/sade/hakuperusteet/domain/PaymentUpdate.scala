package fi.vm.sade.hakuperusteet.domain


import fi.vm.sade.hakuperusteet.domain.PaymentState.PaymentState

case class PaymentUpdate(paymentState: PaymentState)

object PaymentState extends Enumeration {
  type PaymentState = Value
  val NOTIFIED, OK, NOT_OK = Value
}
