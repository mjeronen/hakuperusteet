package fi.vm.sade

import java.text.SimpleDateFormat
import java.time.{LocalDateTime, ZoneId, LocalDate, Instant}
import java.time.format.DateTimeFormatter

import fi.vm.sade.hakuperusteet.domain.PaymentStatus
import fi.vm.sade.hakuperusteet.domain.PaymentStatus.PaymentStatus
import org.json4s.DefaultJsonFormats._

import org.json4s.{DefaultFormats, CustomSerializer}
import java.util.{TimeZone, Date}

import org.json4s.JsonAST.{JInt, JValue, JString, JNull}
import org.json4s.native.Serialization

package object hakuperusteet {
  type Oid = String

  case object DateSerializer extends CustomSerializer[Date](format => (
    {
      case JString(s) => new Date(s.toLong)
      case JInt(s) => new Date(s.toLong)
      case JNull => null
    },
    {
      case d: Date => JInt(d.getTime)
    }
    )
  )

  case object UiDateSerializer extends CustomSerializer[Date](format => (
    {
      case JString(s) => Date.from(LocalDate.parse(s, UIDateFormatter).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
      case JNull => null
    },
    {
      case d: Date => JString(UIDateFormatter.format(
        LocalDateTime.ofInstant(Instant.ofEpochMilli(d.getTime()), ZoneId.systemDefault())))
    }
    )
  )

  val formatsHenkilo = Serialization.formats(org.json4s.NoTypeHints) + DateSerializer

  val formatsUI = new DefaultFormats {
    override def dateFormatter = {
      val df = new SimpleDateFormat("ddMMyyyy")
      df.setTimeZone(TimeZone.getDefault)
      df
    }
  } + UiDateSerializer

  case object PaymentStatusSerializer extends CustomSerializer[PaymentStatus](format => (
    { case JString(s) => PaymentStatus.withName(s) },
    { case x: PaymentStatus => JString(x.toString) })
  )

  implicit val formats = org.json4s.DefaultFormats + PaymentStatusSerializer
  val UIDateFormatter = DateTimeFormatter.ofPattern("ddMMyyyy")
  val personIdDateFormatter = DateTimeFormatter.ofPattern("ddMMyy")

}
