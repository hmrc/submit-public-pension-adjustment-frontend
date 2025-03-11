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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import generators.Generators
import models.{Done, UniqueId}
import models.finalsubmission.FinalSubmissionResponse
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.TestData
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NOT_FOUND, NO_CONTENT, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import scala.concurrent.Future

class SubmitBackendConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.submit-public-pension-adjustment.port" -> server.port)
      .build()

  ".sendFinalSubmission" - {

    "must return OK when submission successful" in {
      val url                  = s"/submit-public-pension-adjustment/final-submission"
      val app                  = application
      val responseJson: String = Json.toJson(FinalSubmissionResponse("ref")).toString()
      val submission           = TestData.finalSubmission1
      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse().withBody(responseJson).withStatus(OK))
        )

        val result: FinalSubmissionResponse = connector.sendFinalSubmission(submission).futureValue

        result `mustBe` FinalSubmissionResponse("ref")
      }
    }

    "must return an upstream error response when fail" in {
      val url        = s"/submit-public-pension-adjustment/final-submission"
      val app        = application
      val submission = TestData.finalSubmission1
      running(app) {

        val connector = app.injector.instanceOf[SubmitBackendConnector]
        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(aResponse.withStatus(500))
        )

        val result = connector.sendFinalSubmission(submission).failed.futureValue

        result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }

  ".sendSubmissionSignal" - {

    "must return true when the submission signal is successfully sent" in {
      val uniqueId = Some(UniqueId("SomeUniqueId"))
      val url      = s"/submit-public-pension-adjustment/submission-signal/${uniqueId.get.value}"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = connector.sendSubmissionSignal(uniqueId).futureValue

        result `mustBe` true
      }
    }

    "Handle a not found exception" in {
      val uniqueId = Some(UniqueId("InvalidUniqueId"))
      val url      = s"/submit-public-pension-adjustment/submission-signal/${uniqueId.get.value}"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )

        val resultFut: Future[Boolean] = connector.sendSubmissionSignal(uniqueId)

        whenReady(resultFut.failed) { e =>
          e `mustBe` a[UpstreamErrorResponse]
        }
      }
    }
  }

  ".sendCalcUserAnswerSignal" - {

    "must return true when the submission signal is successfully sent" in {
      val uniqueId = Some(UniqueId("SomeUniqueId"))
      val url      = s"/submit-public-pension-adjustment/calc-user-answers-signal/${uniqueId.get.value}"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = connector.sendCalcUserAnswerSignal(uniqueId).futureValue

        result `mustBe` true
      }
    }

    "Handle a not found exception" in {
      val uniqueId = Some(UniqueId("InvalidUniqueId"))
      val url      = s"/submit-public-pension-adjustment/calc-user-answers-signal/${uniqueId.get.value}"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )

        val resultFut: Future[Boolean] = connector.sendCalcUserAnswerSignal(uniqueId)

        whenReady(resultFut.failed) { e =>
          e `mustBe` a[UpstreamErrorResponse]
        }
      }
    }
  }

  "clearCalcUserAnswersSubmitBE" - {

    "must return Done when no content found" in {

      val url      = "/submit-public-pension-adjustment/calc-user-answers"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .delete(urlEqualTo(url))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        val result = connector.clearCalcUserAnswersSubmitBE().futureValue

        result `mustBe` Done
      }
    }

    "must return an upstream error response when any other status" in {

      val url      = "/submit-public-pension-adjustment/calc-user-answers"
      val app      = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock
            .delete(urlEqualTo(url))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val result = connector.clearCalcUserAnswersSubmitBE().failed.futureValue

        result `mustBe` a[UpstreamErrorResponse]
      }

    }

  }
}
