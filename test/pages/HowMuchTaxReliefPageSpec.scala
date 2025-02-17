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

import models.calculation.inputs.Income.AboveThreshold
import models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
import models.calculation.inputs.*
import models.calculation.response.{CalculationResponse, TaxYearScheme, TotalAmounts}
import models.submission.Submission
import models.{BankDetails, CheckMode, NormalMode}
import pages.TestData.incomeSubJourney

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

      "to CYA when single scheme" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, ltaOnlySubmissionWithSingleScheme).url

        checkNavigation(result, "/check-your-answers")
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

      "to WhichPensionSchemeWillPay when there are multiple schemes" in {
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

      "to CYA when there is a single scheme" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .success
          .value

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, ltaOnlySubmissionWithSingleScheme).url

        checkNavigation(result, "/check-your-answers")
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

    "ramp on to BAVF when member is in credit and only 1 scheme" in {

      val ua = emptyUserAnswers
        .set(
          HowMuchTaxReliefPage,
          BigInt("100")
        )
        .success
        .value

      val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionInCreditWithOneScheme).url

      checkNavigation(result, "/bavf-ramp-on/normal-mode")
    }

    "to CYA when member is in credit and only 1 scheme" in {

      val ua = emptyUserAnswers
        .set(
          HowMuchTaxReliefPage,
          BigInt("100")
        )
        .success
        .value

      val result = HowMuchTaxReliefPage.navigate(NormalMode, ua, submissionNotInCreditWithOneScheme).url

      checkNavigation(result, "/check-your-answers")
    }

    "must navigate correctly in CheckMode" - {

      "to WhichPensionSchemeWillPay when there are multiple schemes" in {
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
            BankDetails("Testuser One", "111111", "11111111", None)
          )
          .success
          .value

        val cleanedUserAnswers = HowMuchTaxReliefPage.cleanup(Some(BigInt("100")), ua).success.value
        cleanedUserAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) `mustBe` None
        cleanedUserAnswers.get(BankDetailsPage) `mustBe` None

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/submission-service/change-which-pension-scheme-will-pay-tax-relief")
      }

      "ramp on to bavf when there is a single scheme and member is in credit" in {
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
            BankDetails("Testuser One", "111111", "11111111", None)
          )
          .success
          .value

        val cleanedUserAnswers = HowMuchTaxReliefPage.cleanup(Some(BigInt("100")), ua).success.value
        cleanedUserAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) `mustBe` None
        cleanedUserAnswers.get(BankDetailsPage) `mustBe` None

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionInCreditWithOneScheme).url

        checkNavigation(result, "/bavf-ramp-on/check-mode")
      }

      "to CYA when there is a single scheme and member is not in credit" in {
        val ua = emptyUserAnswers
          .set(
            HowMuchTaxReliefPage,
            BigInt("100")
          )
          .get

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionNotInCreditWithOneScheme).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = HowMuchTaxReliefPage.navigate(CheckMode, ua, submissionWithMultipleSchemes).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }

  private def ltaOnlySubmissionWithMultipleSchemes =
    Submission("id", "submissionUniqueId", ltaOnlyCalculationInputsWithMultipleSchemes, None)

  private def ltaOnlySubmissionWithSingleScheme =
    Submission("id", "submissionUniqueId", ltaOnlyCalculationInputsWithSingleScheme, None)

  private def ltaOnlySubmissionWithNoSchemes =
    Submission("id", "submissionUniqueId", ltaOnlyCalculationInputsWithNoSchemes, None)

  private def submissionWithMultipleSchemes = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(
            models.calculation.response.Period._2021,
            320,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            List.empty,
            Some(0)
          )
      )
    )

    val submission: Submission =
      Submission(
        "id",
        "submissionUniqueId",
        calculationInputsWithMultipleSchemes,
        Option(calculationResponse)
      )
    submission
  }

  private def submissionInCreditWithOneScheme = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 0, 1),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(
            models.calculation.response.Period._2021,
            320,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            List.empty,
            Some(0)
          )
      )
    )

    val submission: Submission =
      Submission(
        "id",
        "submissionUniqueId",
        calculationInputsWithSingleScheme,
        Option(calculationResponse)
      )
    submission
  }

  private def submissionNotInCreditWithOneScheme = {
    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List(
        models.calculation.response
          .InDatesTaxYearsCalculation(
            models.calculation.response.Period._2021,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            List.empty,
            Some(0)
          )
      )
    )

    val submission: Submission =
      Submission(
        "id",
        "submissionUniqueId",
        calculationInputsWithSingleScheme,
        Option(calculationResponse)
      )
    submission
  }

  private def ltaOnlyCalculationInputsWithSingleScheme =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      models.calculation.inputs.Setup(
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
      Some(TestData.lifeTimeAllowanceWithSingeScheme)
    )

  private def ltaOnlyCalculationInputsWithMultipleSchemes =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      models.calculation.inputs.Setup(
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
      Some(TestData.lifeTimeAllowanceWithMultipleSchemes)
    )

  private def calculationInputsWithSingleScheme =
    CalculationInputs(
      models.calculation.inputs.Resubmission(false, None),
      models.calculation.inputs.Setup(
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
      Option(
        models.calculation.inputs.AnnualAllowance(
          List(),
          List(
            NormalTaxYear(
              2,
              List(TaxYearScheme("Scheme1", "00348916RT", 2, 0, None)),
              5,
              0,
              models.calculation.inputs.Period._2021,
              incomeSubJourney,
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
      models.calculation.inputs.Setup(
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
      Option(
        models.calculation.inputs.AnnualAllowance(
          List(),
          List(
            NormalTaxYear(
              2,
              List(TaxYearScheme("Scheme1", "00348916RT", 2, 0, Some(4))),
              5,
              0,
              models.calculation.inputs.Period._2016,
              incomeSubJourney,
              None
            ),
            NormalTaxYear(
              5,
              List(TaxYearScheme("Scheme1", "00348916RL", 5, 7, None)),
              8,
              6,
              models.calculation.inputs.Period._2017,
              incomeSubJourney,
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
      models.calculation.inputs.Setup(
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
      Option(
        models.calculation.inputs.LifeTimeAllowance(
          benefitCrystallisationEventDate = LocalDate.of(2017, 1, 30),
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
