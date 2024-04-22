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
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import models.calculation.inputs.{CalculationInputs, Resubmission}
import models.submission.Submission
import models.{Done, UserAnswers}
import org.scalatest.concurrent.ScalaFutures
import pages.TestData
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT, OK}
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.ExecutionContext.Implicits.global

class CalculateBackendConnectorSpec extends SpecBase with ScalaFutures with WireMockHelper {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()
  val wiremockStubPortCalcBE                     = 12802
  val serverCalcBE: WireMockServer               = new WireMockServer(wireMockConfig().port(wiremockStubPortCalcBE))

  override def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }

  private def application: Application =
    applicationBuilder()
      .configure(
        "microservice.services.calculate-public-pension-adjustment.port" -> server.port()
      )
      .build()

  "clearCalcUsersAnswersBE" - {
    "must return Done when the server responds with NO_CONTENT" in {
      val app = application

      server.stubFor(
        delete(urlEqualTo("/calculate-public-pension-adjustment/user-answers"))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.clearCalcUserAnswersBE().futureValue

        result mustBe Done
      }
    }

    "must return a failed future when the server responds with an error status" in {
      val app = application

      server.stubFor(
        delete(urlEqualTo("/calculate-public-pension-adjustment/user-answers"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.clearCalcUserAnswersBE().failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }

  }

  "clearCalcSubmissionsBE" - {
    "must return Done when the server responds with NO_CONTENT" in {
      val app = application

      server.stubFor(
        delete(urlEqualTo("/calculate-public-pension-adjustment/submission"))
          .willReturn(aResponse().withStatus(NO_CONTENT))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.clearCalcSubmissionBE().futureValue

        result mustBe Done
      }
    }

    "must return a failed future when the server responds with an error status" in {
      val app = application

      server.stubFor(
        delete(urlEqualTo("/calculate-public-pension-adjustment/submission"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.clearCalcSubmissionBE().failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }

  "sendFlagResetSignal" - {
    "must return Done when the server responds with OK" in {
      val app = application

      server.stubFor(
        get(urlEqualTo("/calculate-public-pension-adjustment/submission-status-update/1234"))
          .willReturn(aResponse().withStatus(OK))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.sendFlagResetSignal("1234").futureValue

        result mustBe Done
      }
    }

    "must return a failed future when the server responds with an error status" in {
      val app = application

      server.stubFor(
        get(urlEqualTo("/calculate-public-pension-adjustment/submission-status-update/1234"))
          .willReturn(aResponse().withStatus(BAD_REQUEST))
      )

      running(app) {
        val connector = app.injector.instanceOf[CalculateBackendConnector]
        val result    = connector.clearCalcSubmissionBE().failed.futureValue

        result mustBe an[uk.gov.hmrc.http.UpstreamErrorResponse]
      }
    }
  }
}
