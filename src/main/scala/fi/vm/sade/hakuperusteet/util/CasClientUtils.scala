package fi.vm.sade.hakuperusteet.util

import com.typesafe.scalalogging.LazyLogging
import org.http4s.headers.`Content-Type`
import org.http4s._
import org.json4s.Formats
import org.json4s.native.Serialization._

import scalaz.\/._
import scalaz.concurrent.{Future, Task}

trait CasClientUtils extends LazyLogging {
  def urlToUri(url: String) = new Task(Future.now(Uri.fromString(url).leftMap((fail: ParseFailure) => new IllegalArgumentException(fail.sanitized)))).run

  def parseJson4s[A](json: String)(implicit formats: Formats, mf: Manifest[A]) = scala.util.Try(read[A](json)).map(right).recover {
    case t =>
      logger.error("json decoding failed " + json, t)
      left(ParseFailure("json decoding failed", t.getMessage))
  }.get

  def json4sEncoderOf[A <: AnyRef](implicit formats: Formats, mf: Manifest[A]): EntityEncoder[A] = EntityEncoder.stringEncoder(Charset.`UTF-8`).contramap[A](item => write[A](item))
    .withContentType(`Content-Type`(MediaType.`application/json`))

  def json4sOf[A](implicit formats: Formats, mf: Manifest[A]): EntityDecoder[A] = EntityDecoder.decodeBy[A](MediaType.`application/json`) { (msg) =>
    DecodeResult(EntityDecoder.decodeString(msg)(Charset.`UTF-8`).map(parseJson4s[A]))
  }
}
