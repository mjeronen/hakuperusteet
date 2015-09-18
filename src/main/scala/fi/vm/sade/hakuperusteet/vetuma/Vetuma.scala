package fi.vm.sade.hakuperusteet.vetuma

import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.domain.Payment
import org.apache.commons.codec.digest.DigestUtils

case class Vetuma(sharedSecret: String, host: String, timestamp: Date, language: String, returnUrl: String, cancelUrl: String,
                     errorUrl: String, appName: String, amount: String, ref: String, orderNumber: String,
                     msgBuyer: String, msgSeller: String, msgForm: String) {

  val dtf = new SimpleDateFormat("yyyyMMddHHmmssSSS")
  val rcvid = "TESTIASIAKAS11"
  val appid = "PAYMENT-APP2"
  val so = ""
  val solist = "P,L"
  val `type` = "PAYMENT"
  val au = "PAY"
  val ap = "TESTIASIAKAS1"

  private def formatTime = dtf.format(timestamp)

  private def plainText =
    s"$rcvid&$appid&$formatTime&$so&$solist&${`type`}&$au&$language&$returnUrl&$cancelUrl&$errorUrl&" +
    s"$ap&$appName&$amount&$ref&$orderNumber&$msgBuyer&$msgSeller&$msgForm&$sharedSecret&"

  private def mac = DigestUtils.sha256Hex(plainText).toUpperCase

  private def query =
    s"RCVID=$rcvid&APPID=$appid&TIMESTMP=$formatTime&SO=$so&SOLIST=$solist&TYPE=${`type`}&AU=$au&LG=$language&" +
    s"RETURL=$returnUrl&CANURL=$cancelUrl&ERRURL=$errorUrl&AP=$ap&APPNAME=${enc(appName)}&AM=$amount&REF=$ref&" +
    s"ORDNR=$orderNumber&MSGBUYER=${enc(msgBuyer)}&MSGSELLER=${enc(msgSeller)}&MSGFORM=${enc(msgForm)}"

  private def enc(value: String) = URLEncoder.encode(value, "UTF-8")

  def toUrl = s"$host?$query&MAC=$mac"
}

object Vetuma extends LazyLogging {

  def apply(config: Config, payment: Payment, language: String): Vetuma = {
    Vetuma(
      config.getString("vetuma.shared.secret"),
      config.getString("vetuma.host"),
      payment.timestamp,
      language,
      config.getString("vetuma.success.url"),
      config.getString("vetuma.cancel.url"),
      config.getString("vetuma.error.url"),
      config.getString("vetuma.app.name"),
      config.getString("vetuma.amount"),
      payment.reference,
      payment.orderNumber,
      config.getString("vetuma.msg.buyer"),
      config.getString("vetuma.msg.seller"),
      config.getString("vetuma.msg.form")
    )
  }

  def verifyReturnMac(sharedSecret: String, orderedParams: List[String], expectedMac: String) = {
    val plainText = (orderedParams ++ List(sharedSecret, "")).mkString("&")
    val calculatedMac = DigestUtils.sha256Hex(plainText).toUpperCase
    val ok = calculatedMac == expectedMac
    if (!ok) {
      logger.warn(s"Invalid Vetuma return mac $calculatedMac - expected mac $expectedMac")
    }
    ok
  }
}