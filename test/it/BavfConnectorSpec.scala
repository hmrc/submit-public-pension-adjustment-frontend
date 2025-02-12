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
import connectors.{BavfConnector, WireMockHelper}
import models.BankDetails
import models.bavf.{BavfCompleteResponse, BavfInitResponse, BavfPersonalCompleteResponse, ReputationResponseEnum}
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global

class BavfConnectorSpec extends SpecBase with WireMockHelper with Matchers {

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    new GuiceApplicationBuilder()
      .configure("microservice.services.bank-account-verification-web.port" -> server.port)
      .build()

  "initialiseJourney" - {

    "must return the correct URL when the server responds with OK" in {
      val app = application
      running(app) {

        val url                = "/api/v3/init"
        val expectedDetailsUrl = "/details-url"
        val expectedResponse   = BavfInitResponse("1234", "start-url", "complete-url", Some(expectedDetailsUrl))
        val bankDetails        = new BankDetails("accountName", "sortCode", "accountNumber", None)

        val connector = app.injector.instanceOf[BavfConnector]

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(OK)
                .withBody(Json.toJson(expectedResponse).toString())
            )
        )

        val result = connector.initialiseJourney("/some-redirectUrl", Some(bankDetails)).futureValue

        result mustBe s"${connector.bavfWebBaseUrl}$expectedDetailsUrl"
      }
    }

    "must fail when the server responds with an error" in {
      val app = application
      running(app) {

        val url         = "/api/v3/init"
        val bankDetails = new BankDetails("accountName", "sortCode", "accountNumber", None)

        val connector = app.injector.instanceOf[BavfConnector]

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(INTERNAL_SERVER_ERROR)
            )
        )

        val result = connector.initialiseJourney("/some-redirectUrl", Some(bankDetails)).failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }

  "retrieveBarsDetails" - {

    "must return a confirmed address when the server responds with OK" in {

      val responseBody =
        s"""{
           |    "accountType" : "personal",
           |    "personal" : {
           |        "accountName" : "Test Man",
           |        "sortCode" : "sortcode",
           |        "accountNumber" : "accountNumber",
           |        "accountNumberIsWellFormatted":"yes",
           |        "accountExists":"yes",
           |        "nameMatches":"yes",
           |        "nonStandardAccountDetailsRequiredForBacs":"no",
           |        "sortCodeBankName":"BARCLAYS BANK UK PLC",
           |        "sortCodeSupportsDirectDebit":"yes",
           |        "sortCodeSupportsDirectCredit":"yes",
           |        "iban":"GB21BARC20710644344677"
           |    }
           |}""".stripMargin

      val app = application
      running(app) {

        val connector = app.injector.instanceOf[BavfConnector]
        val url       = "/api/v3/complete/1234"

        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(ok(responseBody))
        )

        val result = connector.retrieveBarsDetails("1234").futureValue

        result mustBe BavfCompleteResponse(
          "personal",
          Some(
            BavfPersonalCompleteResponse(
              None,
              "Test Man",
              "sortcode",
              "accountNumber",
              ReputationResponseEnum.Yes,
              None,
              Some(ReputationResponseEnum.Yes),
              Some(ReputationResponseEnum.Yes),
              None,
              Some(ReputationResponseEnum.No),
              Some("BARCLAYS BANK UK PLC"),
              Some(ReputationResponseEnum.Yes),
              Some(ReputationResponseEnum.Yes),
              Some("GB21BARC20710644344677")
            )
          ),
          None
        )
      }
    }

    "must error a result when the server responds with any other status" in {

      val app = application
      running(app) {
        val connector = app.injector.instanceOf[BavfConnector]
        val url       = "/api/v3/complete/1234"
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val result = connector.retrieveBarsDetails("1234").failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }

}
