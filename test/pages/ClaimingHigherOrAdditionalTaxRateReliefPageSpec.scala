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

import models.calculation.inputs.{AnnualAllowance, CalculationInputs, LifeTimeAllowance, Resubmission}
import models.calculation.response.{CalculationResponse, Period, TotalAmounts}
import models.submission.Submission
import models.{BankDetails, CheckMode, NormalMode}
import org.mockito.MockitoSugar.mock

class ClaimingHigherOrAdditionalTaxRateReliefPageSpec extends PageBehaviours {

  val mockCalculationInputsWithAA = CalculationInputs(mock[Resubmission], Some(mock[AnnualAllowance]), None)
  val ltaOnlySubmission           = Submission(
    "sessionId",
    "submissionUniqueId",
    CalculationInputs(mock[Resubmission], None, Some(mock[LifeTimeAllowance])),
    None
  )

  "ClaimingHigherOrAdditionalTaxRateReliefPage" - {

    beRetrievable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beSettable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beRemovable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    "must navigate correctly in NormalMode" - {

      "to how much tax relief when answered yes and LTA only" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/submission-service/how-much-tax-relief-claiming-for")
      }

      "to declarations when answered no and LTA only" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/declarations")
      }

      "to journey recovery when not answered and LTA only" in {
        val ua = emptyUserAnswers

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to BankDetails when answered no and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submissionInCredit).url

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

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submissionNotInCredit).url

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

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/how-much-tax-relief-claiming-for")
      }

      "to JourneyRecovery when not answered" in {
        val ua = emptyUserAnswers

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to change how much tax relief when answered yes and LTA only" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/submission-service/how-much-tax-relief-claiming-for")
      }

      "to Declarations answered no and LTA only" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/declarations")
      }

      "to journey recovery when not answered and LTA only" in {
        val ua = emptyUserAnswers

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, ltaOnlySubmission).url

        checkNavigation(result, "/there-is-a-problem")
      }

      "to HowMuchTaxReliefPage when answered yes and member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submissionNotInCredit).url

        checkNavigation(result, "/submission-service/how-much-tax-relief-claiming-for")
      }

      "to HowMuchTaxReliefPage when answered yes and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submissionInCredit).url

        checkNavigation(result, "/submission-service/how-much-tax-relief-claiming-for")
      }

      "to Declarations when answered no and member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submissionNotInCredit).url

        checkNavigation(result, "/declarations")
      }

      "to BankDetails when answered no and member is in credit" in {
        val ua = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submissionInCredit).url

        checkNavigation(result, "/bank-details")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {
      "must cleanup correctly when answered no" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "testString"
          )
          .success
          .value
          .set(
            BankDetailsPage,
            BankDetails("Testuser One", "111111", "11111111")
          )
          .success
          .value

        val cleanedUserAnswers = ClaimingHigherOrAdditionalTaxRateReliefPage.cleanup(Some(false), ua).success.value
        cleanedUserAnswers.get(HowMuchTaxReliefPage) mustBe None
        cleanedUserAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) mustBe None
        cleanedUserAnswers.get(BankDetailsPage) mustBe None
      }

      "must cleanup correctly when answered yes" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value
          .set(
            WhichPensionSchemeWillPayTaxReliefPage,
            "testString"
          )
          .success
          .value
          .set(
            BankDetailsPage,
            BankDetails("Testuser One", "111111", "11111111")
          )
          .success
          .value

        val cleanedUserAnswers = ClaimingHigherOrAdditionalTaxRateReliefPage.cleanup(Some(true), ua).success.value
        cleanedUserAnswers.get(HowMuchTaxReliefPage) mustBe None
        cleanedUserAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) mustBe None
        cleanedUserAnswers.get(BankDetailsPage) mustBe None
      }
    }
  }

  private def submissionInCredit = {
    val period: Period = Period._2021

    val calculationResponse    = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(models.calculation.response.InDatesTaxYearsCalculation(period, 320, 0, 0, 0, 0, 0, 0, 0, List.empty))
    )
    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
    submission
  }

  private def submissionNotInCredit = {
    val period: Period = Period._2021

    val calculationResponse    = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(models.calculation.response.InDatesTaxYearsCalculation(period, 0, 0, 0, 0, 0, 0, 0, 0, List.empty))
    )
    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))
    submission
  }
}
