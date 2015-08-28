package fi.vm.sade.hakuperusteet

import java.net.URLEncoder

import org.http4s.headers.{`Set-Cookie`, Location}
import org.scalatest.{Matchers, FlatSpec}
import org.http4s.Uri
import org.http4s._
import org.http4s.client.Client
import org.http4s.dsl._

import scalaz.concurrent.Task
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization.{read, write}

class HenkiloClientSpec extends FlatSpec with Matchers {
  val virkailijaUri: Uri = Uri(path = "https://localhost")

  behavior of "HenkiloClient"

  it should "send and receive json with CAS headers in place" in {
    val casParams = CasParams("/authentication-service", "foo", "bar")
    val casMock = new CasMock(virkailijaUrl = virkailijaUri, params = casParams)
    val mock = new Client {
      override def shutdown(): Task[Unit] = Task.now[Unit] {}
      override def prepare(req: Request): Task[Response] = req match {
        case req@ POST -> Root / "authentication-service" / "resources" / "s2s" / "hakuperusteet" =>
          casMock.addStep("valid session")
          Ok("""{"personOid":"1.2.3.4","email":"","firstName":"","lastName":"","birthDate":1440742941926,"gender":"","nationality":"FI","idpentityid":""}""")
        case _ =>
          casMock.addStep("invalid request")
          NotFound()
      }
    }
    val client = new CasAbleClient(new CasClient(virkailijaUri, casMock),
      CasParams("/authentication-service", "foo", "bar"), mock)
    val henkiloClient = new HenkiloClient(virkailijaUri, client)

    val henkilo:User = henkiloClient .haeHenkilo(List(User.empty(""))).run

    henkilo.personOid.get shouldEqual "1.2.3.4"
    henkilo.personId shouldEqual None
    henkilo.birthDate shouldEqual new java.util.Date(1440742941926L)

    casMock.steps should be (List(
      "created TGT-123",
      "created ST-123",
      "created session foobar-123",
      "valid session"
    ))
  }
}



class CasMock(var ticket: String = "123",
              virkailijaUrl: Uri = Uri(path = "https://localhost"),
              params: CasParams) extends Client {
  var steps: List[String] = List()
  def addStep(step: String) = steps = steps :+ step

  override def shutdown(): Task[Unit] = Task.now[Unit] {}

  override def prepare(req: Request): Task[Response] = req match {
    case req@ POST -> Root / "cas" / "v1" / "tickets" => req.decode[String] {
      case body if body == s"username=${params.username}&password=${params.password}" =>
        addStep(s"created TGT-$ticket")
        Created().withHeaders(Location(Uri(path = s"${virkailijaUrl.toString()}/cas/v1/tickets/TGT-$ticket")))
      case _ =>
        addStep(s"invalid login")
        Unauthorized(Challenge("", ""))
    }

    case req@ POST -> Root / "cas" / "v1" / "tickets" / tgt if tgt == s"TGT-$ticket" => req.decode[String] {
      case service if service == s"service=${URLEncoder.encode(s"${virkailijaUrl.toString()}${params.service}/j_spring_cas_security_check", "UTF8")}" =>
        addStep(s"created ST-$ticket")
        Ok(s"ST-$ticket")
      case _ =>
        addStep("invalid TGT url")
        BadRequest()
    }

    case req@ GET -> Root / service / "j_spring_cas_security_check" if params.service.indexOf(service) > -1 => req.queryString match {
      case s if s == s"ticket=ST-$ticket" =>
        addStep(s"created session foobar-$ticket")
        Ok().withHeaders(`Set-Cookie`(Cookie(name = "JSESSIONID", content = s"foobar-$ticket")))
      case _ =>
        addStep("invalid service ticket")
        Unauthorized(Challenge("", ""))
    }
  }

}
