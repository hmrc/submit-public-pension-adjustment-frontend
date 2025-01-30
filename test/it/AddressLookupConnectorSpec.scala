/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import connectors.{AddressLookupConnector, WireMockHelper}
import models.requests.{AddressLookupAddress, AddressLookupConfirmation, AddressLookupCountry}
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.ACCEPTED
import play.api.i18n.Lang
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

class AddressLookupConnectorSpec extends SpecBase with WireMockHelper with Matchers {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.address-lookup-frontend.port" -> server.port)
      .build()

  "initialiseJourney" - {

    "must return a result when the server responds with Accepted" in {
      val app = application
      running(app) {

        val url       = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(ACCEPTED).withHeader("LOCATION", "/some-redirectUrl"))
        )

        val request = FakeRequest("GET", "/url")

        val lang = Lang("en")

        val result = connector.initialiseJourney(request, true, "/some-redirectUrl")(hc, lang).futureValue

        result mustBe "/some-redirectUrl"
      }
    }

    "must error a result when the server responds with any other status" in {
      val app = application
      running(app) {

        val url       = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val request = FakeRequest("GET", "/url")

        val lang = Lang("en")

        val result = connector.initialiseJourney(request, true, "/fail")(hc, lang).failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }

    "must error a result when the server responds ACCEPTED but no location" in {
      val app = application
      running(app) {

        val url       = "/api/init"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(ACCEPTED))
        )

        val request = FakeRequest("GET", "/url")

        val lang = Lang("en")

        val result = connector.initialiseJourney(request, true, "/some-rediectUrl")(hc, lang).failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
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

        val url       = "/api/confirmed?id=1738"
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
            AddressLookupCountry("GB", "United Kingdom")
          )
        )
      }
    }

    "must error a result when the server responds with any other status" in {

      val app = application
      running(app) {

        val url       = "/api/confirmed?id=1738"
        val connector = app.injector.instanceOf[AddressLookupConnector]
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val result = connector.retrieveAddress("1738").failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }
}
