package it

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, ok, post, urlEqualTo}
import connectors.{AddressLookupConnector, WireMockHelper}
import models.requests.{AddressLookupAddress, AddressLookupConfirmation, AddressLookupCountry, AddressLookupOptions, AddressLookupRequest, TimeoutConfig}
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.ACCEPTED
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{HeaderCarrier}

import scala.util.Try

class AddressLookupConnectorSpec extends SpecBase with WireMockHelper with Matchers {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.alf.port" -> server.port)
      .build()

  lazy val alfRequest: AddressLookupRequest = AddressLookupRequest(2,
    AddressLookupOptions(
      continueUrl = "/rampOffUrl",
      timeoutConfig = Some(TimeoutConfig(900, "/timeOut", Some("/keepalive"))),
      disableTranslations = true
    ))


  "start" - {

    "must return a result when the server responds with Accepted" in {
      val app = application
      running(app) {

        val url = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(ACCEPTED).withHeader("LOCATION", "/some-redirectUrl"))
        )

        val result = connector.start(alfRequest).futureValue

        result mustBe ("/some-redirectUrl")
      }
    }

    "must error a result when the server responds with any other status" in {
      val app = application
      running(app) {

        val url = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val result = Try(connector.start(alfRequest).futureValue)

        result.isFailure mustBe true
      }
    }

    "must error a result when the server responds ACCEPTED but no location" in {
      val app = application
      running(app) {

        val url = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(ACCEPTED))
        )

        val result = Try(connector.start(alfRequest).futureValue)

        result.isFailure mustBe true
      }
    }
  }

  "retrieveAddress" - {

    "must return a confirmed address when the server responds with OK" in {

      val responseBody =
        s"""{
           |    "auditRef" : "some-audit-ref",
           |    "id" : "some-id",
           |    "address" : {
           |        "lines" : [ "64 Zoo Lane", "Anyplace", "Anytown" ],
           |        "postcode" : "ZZ1 1ZZ",
           |        "country" : {
           |            "code" : "GB",
           |            "name" : "United Kingdom"
           |        }
           |    }
           |}""".stripMargin

      val app = application
      running(app) {

        val url = "/api/confirmed?id=1738"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(ok(responseBody))
        )

        val result = connector.retrieveAddress("1738").futureValue

        result mustBe AddressLookupConfirmation(
          "some-audit-ref",
          Some("some-id"),
          AddressLookupAddress(
            None,
            List("64 Zoo Lane", "Anyplace", "Anytown"),
            Some("ZZ1 1ZZ"),
            Some(AddressLookupCountry("GB", "United Kingdom"))
          )
        )
      }
    }

    "must error a result when the server responds with any other status" in {

      val app = application
      running(app) {

        val url = "/api/confirmed?id=1738"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val result = Try(connector.retrieveAddress("1738").futureValue)

        result.isFailure mustBe true
      }
    }
  }
}