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
import models.Period
import models.calculation.inputs.CalculationInputs
import models.submission.Submission
import org.mockito.MockitoSugar

class PeriodServiceTest extends SpecBase with MockitoSugar {

  "Period Service" - {

    "should get None for first debit period if there is no calculation response" in {

      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        None
      )

      val firstPeriod: Option[Period] = PeriodService.getFirstDebitPeriod(submission)
      firstPeriod mustBe None
    }

    "should get the first debit period when there is one" in {

      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        Some(aCalculationResponseWithAnInDateDebitYear)
      )

      val firstPeriod: Option[Period] = PeriodService.getFirstDebitPeriod(submission)
      firstPeriod mustBe (Some(Period._2020))
    }

    "should get the next debit period when one exists" in {
      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        Some(aCalculationResponseWithAnInDateDebitYear)
      )

      val nextPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, Period._2021)
      nextPeriod mustBe (Some(Period._2022))
    }

    "should get None when there is no next period" in {
      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        Some(aCalculationResponseWithAnInDateDebitYear)
      )

      val nextPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, Period._2023)
      nextPeriod mustBe None
    }

    "should get None when there is no next debit period because there are no more years in debit" in {
      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        Some(aCalculationResponseWithAnInDateDebitYear)
      )

      val nextPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, Period._2022)
      nextPeriod mustBe None
    }

    "should get None for next debit period if there is no calculation response" in {

      val mockCalculationInputs = mock[CalculationInputs]

      val submission: Submission = Submission(
        "id",
        "sessionId",
        "submissionUniqueId",
        mockCalculationInputs,
        None
      )

      val nextPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, Period._2022)
      nextPeriod mustBe None
    }

  }
}
