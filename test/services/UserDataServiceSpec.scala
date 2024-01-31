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

package services

import base.SpecBase
import connectors.UserAnswersConnector
import models.Done
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import pages.TestData
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class UserDataServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

  "UserDataService" - {
    val hc: HeaderCarrier = HeaderCarrier()

    "get" - {
      "must return Some(UserAnswers) when the connector fetches successfully" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        val expectedUserAnswers = TestData.userAnswers3.copy(lastUpdated = Instant.parse("2024-03-12T10:00:00Z"))

        when(connector.get()(any())) thenReturn Future.successful(Some(expectedUserAnswers))

        val result = service.get()(hc).futureValue
        result mustBe Some(expectedUserAnswers)
      }

      "must return None when the connector fails to fetch" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        when(connector.get()(any())) thenReturn Future.successful(None)

        val result = service.get()(hc).futureValue
        result mustBe None
      }
    }

    "set" - {
      "must return Done when the connector saves successfully" in {
        val connector   = mock[UserAnswersConnector]
        val service     = new UserDataService(connector)
        val userAnswers = emptyUserAnswers

        when(connector.set(any())(any())) thenReturn Future.successful(Done)

        val result = service.set(userAnswers)(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to save" in {
        val connector   = mock[UserAnswersConnector]
        val service     = new UserDataService(connector)
        val userAnswers = emptyUserAnswers

        when(connector.set(any())(any())) thenReturn Future.failed(new RuntimeException("Saving failed"))

        val result = service.set(userAnswers)(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }

    "keepAlive" - {
      "must return Done when the connector updates successfully" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        when(connector.keepAlive()(any())) thenReturn Future.successful(Done)

        val result = service.keepAlive()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to update" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        when(connector.keepAlive()(any())) thenReturn Future.failed(new RuntimeException("Update failed"))

        val result = service.keepAlive()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }

    "clear" - {
      "must return Done when the connector clears successfully" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        when(connector.clear()(any())) thenReturn Future.successful(Done)

        val result = service.clear()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to clear" in {
        val connector = mock[UserAnswersConnector]
        val service   = new UserDataService(connector)

        when(connector.clear()(any())) thenReturn Future.failed(new RuntimeException("Clear failed"))

        val result = service.clear()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }
  }
}
