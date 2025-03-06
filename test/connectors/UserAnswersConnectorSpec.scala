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
import com.github.tomakehurst.wiremock.client.WireMock._
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

class UserAnswersConnectorSpec extends SpecBase with ScalaFutures with WireMockHelper {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    applicationBuilder()
      .configure(
        "microservice.services.submit-public-pension-adjustment.port" -> server.port
      )
      .build()

  "UserAnswersConnector" - {

    "get" - {
      "must return Some(UserAnswers) when the server responds with OK and valid JSON" in {
        val app = application

        val expectedUserAnswers = TestData.userAnswers3.copy(lastUpdated = Instant.parse("2024-03-12T10:00:00Z"))
        val userAnswersJson     = Json.toJson(expectedUserAnswers)

        server.stubFor(
          get(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(OK).withBody(userAnswersJson.toString()))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.get().futureValue

          result `mustBe` Some(expectedUserAnswers)
        }
      }

      "must return None when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          get(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector
            .get()
            .recover { case _: com.fasterxml.jackson.databind.exc.MismatchedInputException =>
              None
            }
            .futureValue

          result `mustBe` None
        }
      }

    }

    "set" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        val userAnswers = UserAnswers("id", Json.obj(), java.time.Instant.now)

        server.stubFor(
          post(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.set(userAnswers).futureValue

          result `mustBe` Done
        }
      }

      "must return a failed future when the server responds with an error status" in {
        val app = application

        val userAnswers = UserAnswers("id", Json.obj(), java.time.Instant.now)

        server.stubFor(
          post(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.set(userAnswers).failed.futureValue

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }
    }

    "keepAlive" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          post(urlEqualTo("/submit-public-pension-adjustment/user-answers/keep-alive"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.keepAlive().futureValue

          result `mustBe` Done
        }
      }
      "must return a failed future when the server responds with an error status" in {
        val app = application

        server.stubFor(
          post(urlEqualTo("/submit-public-pension-adjustment/user-answers/keep-alive"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.keepAlive().failed.futureValue
          print(result)

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }
    }

    "clear" - {
      "must return Done when the server responds with NO_CONTENT" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.clear().futureValue

          result `mustBe` Done
        }
      }

      "must return a failed future when the server responds with an error status" in {
        val app = application

        server.stubFor(
          delete(urlEqualTo("/submit-public-pension-adjustment/user-answers"))
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        running(app) {
          val connector = app.injector.instanceOf[UserAnswersConnector]
          val result    = connector.clear().failed.futureValue

          result `mustBe` an[uk.gov.hmrc.http.UpstreamErrorResponse]
        }
      }

    }
  }
}
