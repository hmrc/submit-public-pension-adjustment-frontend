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

import models.calculation.inputs.{CalculationInputs, ChangeInTaxCharge, ExcessLifetimeAllowancePaid, LtaProtectionOrEnhancements, NewLifeTimeAllowanceAdditions, ProtectionEnhancedChanged, ProtectionType, WhatNewProtectionTypeEnhancement, WhoPaidLTACharge, WhoPayingExtraLtaCharge}
import models.calculation.inputs.Income.AboveThreshold
import models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
import models.calculation.response.{CalculationResponse, TaxYearScheme, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode}

import java.time.LocalDate

class HowMuchTaxReliefPageSpec extends PageBehaviours {

  "HowMuchTaxReliefPage" - {

    beRetrievable[BigInt](HowMuchTaxReliefPage)

    beSettable[BigInt](HowMuchTaxReliefPage)

    beRemovable[BigInt](HowMuchTaxReliefPage)

    "must navigate correctly in NormalMode when LTAOnly" - {

      "to WhichPensionSchemeWillPayTaxReliefPage when multiple schemes" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, ltaOnlySubmissionWithMultipleSchemes).url

        checkNavigation(result, "/submission-service/which-pension-scheme-will-pay-tax-relief")
      }

      "to DeclarationsPage when single scheme" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, ltaOnlySubmissionWithSingleScheme).url

        checkNavigation(result, "/declarations")
      }
    }

    "to journey recovery when no schemes" in {
      val ua = emptyUserAnswers
        .set(
          HowMuchTaxReliefPage,
          BigInt("100")
        )
        .success
        .value

      val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, ltaOnlySubmissionWithNoSchemes).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "must navigate correctly in CheckMode when LTAOnly" - {

      "to WhichPensionSchemeWillPay" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, ltaOnlySubmissionWithMultipleSchemes).url

        checkNavigation(result, "/submission-service/which-pension-scheme-will-pay-tax-relief")
      }
    }

    "to journey recovery when no answer" in {

      val ua = emptyUserAnswers

      val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, ltaOnlySubmissionWithMultipleSchemes).url

      checkNavigation(result, "/there-is-a-problem")
    }

    "must navigate correctly in NormalMode" - {

      "to WhichPensionSchemeWillPayTaxReliefPage" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/submission-service/which-pension-scheme-will-pay-tax-relief")
      }

      "to JourneyRecovery when not answered" in {
        val ua = emptyUserAnswers

        val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "to bank details controller when member is in credit and only 1 scheme" in {

      val ua = emptyUserAnswers
        .set(
          HowMuchTaxReliefPage,
          BigInt("100")
        )
        .success
        .value

      val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionInCreditWithOneScheme).url

      checkNavigation(result, "/bank-details")
    }

    "to declarations controller controller when member is in credit and only 1 scheme" in {

      val ua = emptyUserAnswers
        .set(
          HowMuchTaxReliefPage,
          BigInt("100")
        )
        .success
        .value

      val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionNotInCreditWithOneScheme).url

      checkNavigation(result, "/declarations")
    }

    "must navigate correctly in CheckMode" - {

      "to WhichPensionSchemeWillPay" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/submission-service/which-pension-scheme-will-pay-tax-relief")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }

  private def ltaOnlySubmissionWithMultipleSchemes =
    Submission("sessionId", "submissionUniqueId", ltaOnlyCalculationInputsWithMultipleSchemes, None)

  private def ltaOnlySubmissionWithSingleScheme =
    Submission("sessionId", "submissionUniqueId", ltaOnlyCalculationInputsWithSingleScheme, None)

  private def ltaOnlySubmissionWithNoSchemes =
    Submission("sessionId", "submissionUniqueId", ltaOnlyCalculationInputsWithNoSchemes, None)

  private def submissionWithMultipleSchemes = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(models.calculation.response.Period._2021, 320, 0, 0, 0, 0, 0, 0, 0, List.empty)
      )
    )

    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", calculationInputsWithMultipleSchemes, Option(calculationResponse))
    submission
  }

  private def submissionInCreditWithOneScheme = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 0, 1),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(models.calculation.response.Period._2021, 320, 0, 0, 0, 0, 0, 0, 0, List.empty)
      )
    )

    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", calculationInputsWithSingleScheme, Option(calculationResponse))
    submission
  }

  private def submissionNotInCreditWithOneScheme = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(models.calculation.response.Period._2021, 0, 0, 0, 0, 0, 0, 0, 0, List.empty)
      )
    )

    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", calculationInputsWithSingleScheme, Option(calculationResponse))
    submission
  }

  private def ltaOnlyCalculationInputsWithSingleScheme =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      None,
      Some(TestData.lifeTimeAllowanceWithSingeScheme)
    )

  private def ltaOnlyCalculationInputsWithMultipleSchemes =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      None,
      Some(TestData.lifeTimeAllowanceWithMultipleSchemes)
    )

  private def calculationInputsWithSingleScheme =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      Option(
        models.calculation.inputs.AnnualAllowance(
          List(),
          List(
            NormalTaxYear(
              2,
              List(TaxYearScheme("Scheme1", "00348916RT", 1, 2, 0)),
              5,
              0,
              models.calculation.inputs.Period._2021,
              None
            )
          )
        )
      ),
      None
    )

  private def calculationInputsWithMultipleSchemes =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      Option(
        models.calculation.inputs.AnnualAllowance(
          List(),
          List(
            NormalTaxYear(
              2,
              List(TaxYearScheme("Scheme1", "00348916RT", 1, 2, 0)),
              5,
              0,
              models.calculation.inputs.Period._2016PreAlignment,
              None
            ),
            NormalTaxYear(
              4,
              List(TaxYearScheme("Scheme2", "00348916Rl", 3, 4, 0)),
              5,
              0,
              models.calculation.inputs.Period._2016PostAlignment,
              None
            ),
            NormalTaxYear(
              5,
              List(TaxYearScheme("Scheme1", "00348916RT", 4, 5, 7)),
              8,
              6,
              models.calculation.inputs.Period._2017,
              Some(AboveThreshold(7))
            )
          )
        )
      ),
      None
    )

  private def ltaOnlyCalculationInputsWithNoSchemes =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      None,
      Option(
        models.calculation.inputs.LifeTimeAllowance(
          benefitCrystallisationEventFlag = true,
          benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
          changeInLifetimeAllowancePercentageInformedFlag = true,
          changeInTaxCharge = ChangeInTaxCharge.NewCharge,
          lifetimeAllowanceProtectionOrEnhancements = LtaProtectionOrEnhancements.Protection,
          protectionType = Some(ProtectionType.PrimaryProtection),
          protectionReference = Some("originalReference"),
          ProtectionEnhancedChanged.Protection,
          newProtectionTypeOrEnhancement = Some(WhatNewProtectionTypeEnhancement.EnhancedProtection),
          newProtectionTypeOrEnhancementReference = Some("newReference"),
          previousLifetimeAllowanceChargeFlag = true,
          previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum),
          previousLifetimeAllowanceChargePaidBy = Some(WhoPaidLTACharge.You),
          previousLifetimeAllowanceChargeSchemeNameAndTaxRef = None,
          newLifetimeAllowanceChargeWillBePaidBy = Some(WhoPayingExtraLtaCharge.You),
          newLifetimeAllowanceChargeSchemeNameAndTaxRef = None,
          NewLifeTimeAllowanceAdditions(
            false,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
        )
      )
    )
}
