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

import models.{CheckMode, NormalMode}

class ClaimOnBehalfPageSpec extends PageBehaviours {

  "ClaimOnBehalfPage" - {

    beRetrievable[Boolean](ClaimOnBehalfPage)

    beSettable[Boolean](ClaimOnBehalfPage)

    beRemovable[Boolean](ClaimOnBehalfPage)
  }

  "must redirect to status of user page when user selects yes" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/status-of-user")
  }

  "must redirect to check your answers page when user selects no" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/who-will-pay")
  }

  "must redirect user to change status of user when user resubmits yes in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/change-status-of-user")
  }

  "must redirect user to check your answers page when user resubmits no in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
