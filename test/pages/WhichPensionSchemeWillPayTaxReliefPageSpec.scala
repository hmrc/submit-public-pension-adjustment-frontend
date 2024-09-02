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

package pages

import models.calculation.inputs.{AnnualAllowance, CalculationInputs, LifeTimeAllowance, Resubmission, Setup}
import models.calculation.response.{CalculationResponse, Period, TotalAmounts}
import models.submission.Submission
import models.{BankDetails, CheckMode, NormalMode}
import org.mockito.MockitoSugar.mock

class WhichPensionSchemeWillPayTaxReliefSpec extends PageBehaviours {

  val mockCalculationInputsWithAA      =
    CalculationInputs(mock[Resubmission], mock[Setup], Some(mock[AnnualAllowance]), None)
  val mockCalculationInputsWithLTAOnly =
    CalculationInputs(mock[Resubmission], mock[Setup], None, Some(mock[LifeTimeAllowance]))

  "WhichPensionSchemeWillPayTaxReliefPage" - {

    beRetrievable[String](WhichPensionSchemeWillPayTaxReliefPage)

    beSettable[String](WhichPensionSchemeWillPayTaxReliefPage)

    beRemovable[String](WhichPensionSchemeWillPayTaxReliefPage)

    "must navigate correctly when LTA Only" - {

      "to CYA in NormalMode" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RT"
          )
          .success
          .value

        val result = WhichPensionSchemeWillPayTaxReliefPage
          .navigate(
            NormalMode,
            ua,
            Submission("id", "submissionUniqueId", mockCalculationInputsWithLTAOnly, None)
          )
          .url

        checkNavigation(result, "/check-your-answers")
      }

      "to CYA in CheckMode" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RT"
          )
          .success
          .value

        val result = WhichPensionSchemeWillPayTaxReliefPage
          .navigate(
            CheckMode,
            ua,
            Submission("id", "submissionUniqueId", mockCalculationInputsWithLTAOnly, None)
          )
          .url

        checkNavigation(result, "/check-your-answers")
      }
    }

    "must navigate correctly in NormalMode" - {

      "to BARS when Pension scheme b scheme selected and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RT"
          )
          .success
          .value

        val period: Period = Period._2021

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 320, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )
        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
        val result                 = WhichPensionSchemeWillPayTaxReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/bank-details")
      }

      "to CYA when Pension scheme b scheme selected and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RT"
          )
          .success
          .value

        val period: Period = Period._2021

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 0, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )
        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
        val result                 = WhichPensionSchemeWillPayTaxReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecoveryPage when not selected" in {
        val ua = emptyUserAnswers

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
        val result                 = WhichPensionSchemeWillPayTaxReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RT"
          )
          .success
          .value

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
        val result                 = WhichPensionSchemeWillPayTaxReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to BARS when Pension scheme b scheme selected and member is in credit in Check mode" in {
        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "Scheme1 / 00348916RL"
          )
          .success
          .value
          .set(
            BankDetailsPage,
            BankDetails("Testuser One", "111111", "11111111")
          )
          .success
          .value

        val period: Period = Period._2021

        val calculationResponse = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List(models.calculation.response.InDatesTaxYearsCalculation(period, 320, 0, 0, 0, 0, 0, 0, 0, List.empty))
        )

        val cleanedUserAnswers =
          WhichPensionSchemeWillPayTaxReliefPage.cleanup(Some("Scheme1 / 00348916RL"), ua).success.value
        cleanedUserAnswers.get(BankDetailsPage) mustBe None

        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
        val result                 = WhichPensionSchemeWillPayTaxReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/bank-details")
      }

      "to JourneyRecovery when not answered" in {
        val ua = emptyUserAnswers

        val calculationResponse    = CalculationResponse(
          models.calculation.response.Resubmission(false, None),
          TotalAmounts(0, 1, 0),
          List.empty,
          List.empty
        )
        val submission: Submission =
          Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))

        val result = WhichPensionSchemeWillPayTaxReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
