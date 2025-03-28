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

import models.calculation.inputs.CalculationInputs
import models.submission.Submission
import models.{CheckMode, NormalMode, Period}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.mockito.MockitoSugar.mock

class SchemeElectionConsentPageSpec extends PageBehaviours {

  "SchemeElectionConsentPageSpec" - {

    beRetrievable[Boolean](SchemeElectionConsentPage(Period._2020))

    beSettable[Boolean](SchemeElectionConsentPage(Period._2020))

    beRemovable[Boolean](SchemeElectionConsentPage(Period._2020))
  }

  val mockCalculationInputs = mock[CalculationInputs]

  val userWithDebitSubmission: Submission = Submission(
    "id",
    "submissionUniqueId",
    mockCalculationInputs,
    Some(aCalculationResponseWithAnInDateDebitYear)
  )

  "must navigate correctly in NormalMode" - {

    "to AlternativeNamePage when user submits and no more debit periods" in {
      val ua     = emptyUserAnswers
        .set(
          SchemeElectionConsentPage(Period._2022),
          true
        )
        .success
        .value
      val result =
        SchemeElectionConsentPage(Period._2022).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/submission-service/name-pension-scheme-holds")
    }

    "to WhoWillPayPage when user submits and user has more debit years to submit" in {
      val ua     = emptyUserAnswers
        .set(
          SchemeElectionConsentPage(Period._2020),
          true
        )
        .success
        .value
      val result =
        SchemeElectionConsentPage(Period._2020).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/submission-service/2021/who-will-pay-new-tax-charge")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result =
        SchemeElectionConsentPage(Period._2020).navigate(NormalMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "must navigate correctly in CheckMode" - {

    "to CYA when no more debit periods when submits" in {
      val ua     = emptyUserAnswers
        .set(
          SchemeElectionConsentPage(Period._2022),
          true
        )
        .success
        .value
      val result =
        SchemeElectionConsentPage(Period._2022).navigate(CheckMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/check-your-answers")
    }

    "to CYA when user submits and more debit periods" in {
      val ua     = emptyUserAnswers
        .set(
          SchemeElectionConsentPage(Period._2020),
          true
        )
        .success
        .value
      val result =
        SchemeElectionConsentPage(Period._2020).navigate(CheckMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/check-your-answers")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result =
        SchemeElectionConsentPage(Period._2020).navigate(CheckMode, ua, userWithDebitSubmission).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

}
