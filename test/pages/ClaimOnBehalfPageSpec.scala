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
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode}
import org.mockito.MockitoSugar.mock

class ClaimOnBehalfPageSpec extends PageBehaviours {

  "ClaimOnBehalfPage" - {

    beRetrievable[Boolean](ClaimOnBehalfPage)

    beSettable[Boolean](ClaimOnBehalfPage)

    beRemovable[Boolean](ClaimOnBehalfPage)
  }

  val mockCalculationInputs = mock[CalculationInputs]

  "must redirect to status of user page when user selects yes in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission("sessionId", "submissionUniqueId", mockCalculationInputs, None)
    val nextPageUrl: String    = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/status-of-user")
  }

  "must redirect to enter who will pay page when user selects no and user is in debit in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "sessionId",
      "submissionUniqueId",
      mockCalculationInputs,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/who-will-pay/2020")
  }

  "must redirect to enter alternate name page when user selects no and is in credit/direct comp/indirect comp in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val calculationResponse    = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(1, 0, 0),
      List.empty,
      List.empty
    )
    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/alternative-name")
  }

  "must redirect user to change status of user when user resubmits yes in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission("sessionId", "submissionUniqueId", mockCalculationInputs, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/change-status-of-user")
  }

  "must redirect to who paid charge page when user selects no and user is in debit in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "sessionId",
      "submissionUniqueId",
      mockCalculationInputs,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/change-who-will-pay/2020")
  }

  "must redirect to check your answers page when user selects no and is in credit/direct comp/indirect comp " in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(1, 0, 0),
      List.empty,
      List.empty
    )

    val submission: Submission =
      Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission("sessionId", "submissionUniqueId", mockCalculationInputs, None)

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission("sessionId", "submissionUniqueId", mockCalculationInputs, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
