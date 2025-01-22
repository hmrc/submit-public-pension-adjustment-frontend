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
import connectors.SubmissionsConnector
import models.Done
import models.calculation.inputs._
import models.submission.Submission
import org.mockito.MockitoSugar.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.http.HeaderCarrier

import java.time.Instant
import scala.concurrent.Future

class SubmissionDataServiceSpec extends SpecBase with MockitoSugar with ScalaFutures {

  "UserDataService" - {
    val hc: HeaderCarrier = HeaderCarrier()

    "getByUserId" - {
      "must return Some(UserAnswers) when the connector fetches successfully" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)
        val userId    = "someSessionId"

        val expectedSubmission = Submission(
          id = "id",
          uniqueId = "someUniqueId",
          calculationInputs = CalculationInputs(
            Resubmission(false, None),
            Setup(
              Some(
                AnnualAllowanceSetup(
                  Some(true),
                  Some(false),
                  Some(false),
                  Some(false),
                  Some(false),
                  Some(false),
                  Some(MaybePIAIncrease.No),
                  Some(MaybePIAUnchangedOrDecreased.No),
                  Some(false),
                  Some(false),
                  Some(false),
                  Some(false)
                )
              ),
              Some(
                LifetimeAllowanceSetup(
                  Some(true),
                  Some(false),
                  Some(true),
                  Some(false),
                  Some(false),
                  Some(false),
                  Some(true)
                )
              )
            ),
            None,
            None
          ),
          calculation = None,
          lastUpdated = Instant.parse("2024-03-12T10:00:00Z")
        )
        when(connector.getByUserId(userId)(hc)) thenReturn Future.successful(Some(expectedSubmission))

        val result = service.getByUserId(userId)(hc).futureValue
        result mustBe Some(expectedSubmission)
      }

      "must return None when the connector fails to fetch" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)
        val userId    = "someSessionId"

        when(connector.getByUserId(userId)(hc)) thenReturn Future.successful(None)

        val result = service.getByUserId(userId)(hc).futureValue
        result mustBe None
      }
    }

    "keepAlive" - {
      "must return Done when the connector updates successfully" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.keepAlive()(hc)) thenReturn Future.successful(Done)

        val result = service.keepAlive()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to update" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.keepAlive()(hc)) thenReturn Future.failed(new RuntimeException("Update failed"))

        val result = service.keepAlive()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }

    "clear" - {
      "must return Done when the connector clears successfully" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.clear()(hc)) thenReturn Future.successful(Done)

        val result = service.clear()(hc).futureValue
        result mustBe Done
      }

      "must return a failed future when the connector fails to clear" in {
        val connector = mock[SubmissionsConnector]
        val service   = new SubmissionDataService(connector)

        when(connector.clear()(hc)) thenReturn Future.failed(new RuntimeException("Clear failed"))

        val result = service.clear()(hc).failed.futureValue
        result mustBe a[RuntimeException]
      }
    }
  }
}
