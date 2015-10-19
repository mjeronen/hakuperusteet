package fi.vm.sade.hakuperusteet.util

import fi.vm.sade.auditlog.hakuperusteet.{LogMessage, HakuPerusteetOperation}
import fi.vm.sade.auditlog.{Audit, ApplicationType}
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.domain._

import fi.vm.sade.auditlog.hakuperusteet.LogMessage.builder

object AuditLog {
  val serviceName = "hakuperusteet"
  val audit = new Audit(serviceName, ApplicationType.OPISKELIJA)

  def auditPostUserdata(userData: User) = {
    audit.log(builder()
      .id(userData.personOid.getOrElse("<unknown>"))
      .setOperaatio(HakuPerusteetOperation.USERDATA)
      .email(userData.email)
      .firstName(userData.firstName)
      .lastName(userData.lastName)
      .birthDate(userData.birthDate)
      .personId(userData.personId.getOrElse("<none>"))
      .gender(userData.gender)
      .nativeLanguage(userData.nativeLanguage)
      .nationality(userData.nationality)
      .build()
    )
  }

  def auditPostEducation(userData: User, education: ApplicationObject) = {
    audit.log(builder()
      .id(userData.personOid.getOrElse("<unknown>"))
      .email(userData.email)
      .setOperaatio(HakuPerusteetOperation.EDUCATION)
      .hakuOid(education.hakuOid)
      .hakukohdeOid(education.hakukohdeOid)
      .educationLevel(education.educationLevel)
      .educationCountry(education.educationCountry)
      .build()
    )
  }

  def auditPayment(userData: User, payment: Payment) =
    audit.log(builder()
      .id(userData.personOid.getOrElse("<unknown>"))
      .email(userData.email)
      .setOperaatio(statusToOperation(payment.status))
      .timestamp(payment.timestamp)
      .reference(payment.reference)
      .orderNumber(payment.orderNumber)
      .paymCallId(payment.paymCallId)
      .build()
    )

  private def statusToOperation(status: PaymentStatus) = status match {
    case PaymentStatus.started => HakuPerusteetOperation.PAYMENT_STARTED
    case PaymentStatus.ok => HakuPerusteetOperation.PAYMENT_OK
    case PaymentStatus.cancel => HakuPerusteetOperation.PAYMENT_CANCEL
    case PaymentStatus.error => HakuPerusteetOperation.PAYMENT_ERROR
  }
}
