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
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, urlEqualTo}
import generators.Generators
import models.UniqueId
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.test.Helpers.{await, defaultAwaitTimeout, running}
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException, UpstreamErrorResponse}

import scala.concurrent.Future

class SubmitBackendConnectorSpec extends SpecBase with WireMockHelper with ScalaCheckPropertyChecks with Generators {

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private def application: Application =
    applicationBuilder()
      .configure("microservice.services.submit-public-pension-adjustment.port" -> server.port)
      .build()

  ".sendSubmissionSignal" - {

    "must return true when the submission signal is successfully sent" in {
      val uniqueId = Some(UniqueId("SomeUniqueId"))
      val url = s"/submit-public-pension-adjustment/submission-signal/${uniqueId.get.value}"
      val app = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = connector.sendSubmissionSignal(uniqueId).futureValue

        result mustBe true
      }
    }

    "Handle a not found exception" in {
      val uniqueId = Some(UniqueId("InvalidUniqueId"))
      val url = s"/submit-public-pension-adjustment/submission-signal/${uniqueId.get.value}"
      val app = application

      running(app) {
        val connector = app.injector.instanceOf[SubmitBackendConnector]

        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(aResponse().withStatus(NOT_FOUND))
        )

        val resultFut: Future[Boolean] = connector.sendSubmissionSignal(uniqueId)

        whenReady(resultFut.failed) { e =>
          e mustBe a[NotFoundException]
        }
      }
    }
  }
}