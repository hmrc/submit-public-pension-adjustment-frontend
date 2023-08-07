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
import models.{CheckMode, NormalMode, Period, WhenWillYouAskPensionSchemeToPay}
import org.mockito.MockitoSugar.mock

class WhenWillYouAskPensionSchemeToPayPageSpec extends PageBehaviours {

  "WhenWillYouAskPensionSchemeToPayPage" - {

    beRetrievable[WhenWillYouAskPensionSchemeToPay](WhenWillYouAskPensionSchemeToPayPage(Period._2020))

    beSettable[WhenWillYouAskPensionSchemeToPay](WhenWillYouAskPensionSchemeToPayPage(Period._2020))

    beRemovable[WhenWillYouAskPensionSchemeToPay](WhenWillYouAskPensionSchemeToPayPage(Period._2020))
  }

  val mockCalculationInputs = mock[CalculationInputs]

  val userWithDebitSubmission: Submission = Submission(
    "sessionId",
    "submissionUniqueId",
    mockCalculationInputs,
    Some(aCalculationResponseWithAnInDateDebitYear)
  )

  "must navigate correctly in NormalMode" - {

    "to AlternativeNamePage when user submits and no more debit periods" in {
      val ua     = emptyUserAnswers
        .set(
          WhenWillYouAskPensionSchemeToPayPage(Period._2022),
          WhenWillYouAskPensionSchemeToPay.OctToDec23
        )
        .success
        .value
      val result = WhenWillYouAskPensionSchemeToPayPage(Period._2022).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/alternative-name")
    }

    "to WhoWillPayPage when user submits and user has more debit years to submit" in {
      val ua     = emptyUserAnswers
        .set(
          WhenWillYouAskPensionSchemeToPayPage(Period._2020),
          WhenWillYouAskPensionSchemeToPay.OctToDec23
        )
        .success
        .value
      val result = WhenWillYouAskPensionSchemeToPayPage(Period._2020).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/who-will-pay/2021")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result = WhenWillYouAskPensionSchemeToPayPage(Period._2020).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "must navigate correctly in CheckMode" - {

    "to CYA when selected" in {
      val ua     = emptyUserAnswers
        .set(
          WhenWillYouAskPensionSchemeToPayPage(Period._2020),
          WhenWillYouAskPensionSchemeToPay.JanToMar24
        )
        .success
        .value
      val result = WhenWillYouAskPensionSchemeToPayPage(Period._2020).navigate(CheckMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/check-your-answers")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result = WhenWillYouAskPensionSchemeToPayPage(Period._2020).navigate(CheckMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

}
