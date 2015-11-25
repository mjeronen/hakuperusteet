package fi.vm.sade.hakuperusteet

import java.util.Date
import java.util.concurrent.Executors

import fi.vm.sade.hakuperusteet.admin.Synchronization
import fi.vm.sade.hakuperusteet.domain.IDPEntityId
import fi.vm.sade.hakuperusteet.domain.PaymentState.PaymentState
import fi.vm.sade.hakuperusteet.domain.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import fi.vm.sade.hakuperusteet.hakuapp.HakuAppClient
import org.http4s
import org.json4s.native.JsonMethods._
import org.junit.runner.RunWith
import org.mockito.{Mockito, Mock}
import org.mockito.Matchers._
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite
import fi.vm.sade.hakuperusteet.domain.User._
import fi.vm.sade.hakuperusteet.domain.Payment
import fi.vm.sade.hakuperusteet.domain.PaymentStatus

@RunWith(classOf[JUnitRunner])
class SynchronizationSpec extends FunSuite with ScalatraSuite with ServletTestDependencies {
  val hakuAppMock = Mockito.mock(classOf[HakuAppClient])
  val synchronization = new Synchronization(config, database, null, countries, null) {
    override val scheduler = Executors.newScheduledThreadPool(0)
    override val hakuAppClient = hakuAppMock
  }

  test("haku-app synchronization") {
    val personOid = "4.4.4.4"
    val hakemusOid = "1.1.1.1"
    val email = "e@mail.com"
    val user = database.findUser(email).orElse(database.upsertPartialUser(partialUser(None, Some(personOid), email, IDPEntityId.oppijaToken))).get

    val payment = Payment(None, personOid, new Date(), "1234", "1234", "1234", PaymentStatus.ok,Some(hakemusOid))
    database.upsertPayment(payment)
    database.insertPaymentSyncRequest(user, payment)
    Mockito.when(hakuAppMock.updateHakemusWithPaymentState(anyString(), any[PaymentState])).thenReturn(http4s.Response())
    synchronization.checkTodoSynchronizations.run()
    Mockito.verify(hakuAppMock, Mockito.times(1)).updateHakemusWithPaymentState(anyString(), any[PaymentState])
  }
}
