package fi.vm.sade

import org.json4s.DefaultJsonFormats._

import org.json4s.CustomSerializer
import java.util.Date

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

  val formatsHenkilo = Serialization.formats(org.json4s.NoTypeHints) + DateSerializer

  implicit val formats = org.json4s.DefaultFormats
}
