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
import models.submission.Submission
import models.{CheckMode, NormalMode, Period, WhoWillPay}
import org.mockito.MockitoSugar.mock

class WhoWillPayPageSpec extends PageBehaviours {

  "WhoWillPayPage" - {

    beRetrievable[WhoWillPay](WhoWillPayPage(Period._2020))

    beSettable[WhoWillPay](WhoWillPayPage(Period._2020))

    beRemovable[WhoWillPay](WhoWillPayPage(Period._2020))

    val mockCalculationInputs = mock[CalculationInputs]

    val submission: Submission = Submission(
      "sessionId",
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

        checkNavigation(result, "/which-pension-scheme-will-pay/2020")
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

        checkNavigation(result, "/alternative-name")
      }

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = WhoWillPayPage(Period._2020).navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when selected" in {
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

      "to JourneyRecovery when not selected" in {
        val ua = emptyUserAnswers

        val result = WhoWillPayPage(Period._2020).navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
