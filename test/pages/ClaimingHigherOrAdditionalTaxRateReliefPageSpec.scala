/*
 * Copyright 2023 HM Revenue & Customs
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

package pages

import models.calculation.inputs.CalculationInputs
import models.calculation.response.{CalculationResponse, Period, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode}
import org.mockito.MockitoSugar.mock

class ClaimingHigherOrAdditionalTaxRateReliefPageSpec extends PageBehaviours {

  "ClaimingHigherOrAdditionalTaxRateReliefPage" - {

    beRetrievable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beSettable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beRemovable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    "must navigate correctly in NormalMode" - {

      "to BankDetails when answered no and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val period: Period = Period._2021

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 320, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))
        val result                 = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/bank-details")
      }

      "to Declarations when answered no and member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val period: Period = Period._2021

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 0, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))
        val result                 = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/declarations")
      }

      "to HowMuchTaxReliefPage when answered yes" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))
        val result                 = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/tax-relief-amount")
      }

      "to JourneyRecovery when not answered" in {
        val ua = emptyUserAnswers

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to HowMuchTaxReliefPage when answered yes and member is not in credit " in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/change-tax-relief-amount")
      }

      "to CYA when answered no and member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val period: Period = Period._2021

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 0, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val mockCalculationInputs = mock[CalculationInputs]

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
