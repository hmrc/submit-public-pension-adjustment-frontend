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

import models.WhoWillPay.You
import models.calculation.inputs.CalculationInputs
import models.submission.Submission
import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, WhenWillYouAskPensionSchemeToPay, WhoWillPay}
import org.scalatestplus.mockito.MockitoSugar.mock

import java.time.LocalDate

class WhoWillPayPageSpec extends PageBehaviours {

  "WhoWillPayPage" - {

    beRetrievable[WhoWillPay](WhoWillPayPage(Period._2020))

    beSettable[WhoWillPay](WhoWillPayPage(Period._2020))

    beRemovable[WhoWillPay](WhoWillPayPage(Period._2020))

    val mockCalculationInputs = mock[CalculationInputs]

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputs,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    "must navigate correctly in NormalMode" - {

      "to WhichPensionSchemeWillPayPage when PensionScheme selected" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2020),
            WhoWillPay.PensionScheme
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2020).navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/2020/which-pension-scheme-will-pay-tax-charge")
      }

      "to AlternativeNamePage when You selected and no more debit periods" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2022),
            WhoWillPay.You
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2022).navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/name-pension-scheme-holds")
      }

      "to Who Will Pay Page when You selected and  more debit periods" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2020),
            WhoWillPay.You
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2020).navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/2021/who-will-pay-new-tax-charge")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = WhoWillPayPage(Period._2020).navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when user selects You and more debit periods" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2020),
            WhoWillPay.You
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2020).navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to CYA when user selects You and no more debit periods" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2022),
            WhoWillPay.You
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2022).navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to which pension scheme will pay page when user selects scheme" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage(Period._2022),
            WhoWillPay.PensionScheme
          )
          .success
          .value

        val result = WhoWillPayPage(Period._2022).navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/submission-service/2022/change-which-pension-scheme-will-pay-tax-charge")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = WhoWillPayPage(Period._2020).navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "must clean up correctly when user answers you" - {

        val ua = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayPage(Period._2020),
            "Private pension scheme"
          )
          .success
          .value
          .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
          .success
          .value
          .set(AskedPensionSchemeToPayTaxChargePage(Period._2020), false)
          .success
          .value
          .set(WhenDidYouAskPensionSchemeToPayPage(Period._2020), LocalDate.of(2020, 1, 1))
          .success
          .value
          .set(WhenWillYouAskPensionSchemeToPayPage(Period._2020), WhenWillYouAskPensionSchemeToPay.OctToDec23)
          .success
          .value
          .set(SchemeElectionConsentPage(Period._2020), true)
          .success
          .value

        val cleanedUserAnswers = WhoWillPayPage(Period._2020).cleanup(Some(You), ua).success.value

        cleanedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2020)) `mustBe` None
        cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` None
        cleanedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2020)) `mustBe` None
        cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2020)) `mustBe` None
        cleanedUserAnswers.get(WhenWillYouAskPensionSchemeToPayPage(Period._2020)) `mustBe` None
        cleanedUserAnswers.get(SchemeElectionConsentPage(Period._2020)) `mustBe` None
      }
    }
  }
}
