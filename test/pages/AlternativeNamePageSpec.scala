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

class AlternativeNamePageSpec extends PageBehaviours {

  "AlternativeNamePage" - {

    beRetrievable[Boolean](AlternativeNamePage)

    beSettable[Boolean](AlternativeNamePage)

    beRemovable[Boolean](AlternativeNamePage)
  }

  "must redirect to enter alternative number page when user selects no" in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/enter-name-pension-scheme-holds")
  }

  "must redirect to contact number page when user selects yes" in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/contact-number")
  }

  "must redirect user to check their answers when user resubmits yes in check mode" in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect user to enter alternative name page when user resubmits no in check mode" in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-enter-name-pension-scheme-holds")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode " in {

    val page = AlternativeNamePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "cleanup" - {

    "must cleanup correctly when answered yes" in {
      val ua = emptyUserAnswers
        .set(
          EnterAlternativeNamePage,
          "John Doe"
        )
        .success
        .value

      val cleanedUserAnswers = AlternativeNamePage.cleanup(Some(true), ua).success.value
      cleanedUserAnswers.get(EnterAlternativeNamePage) mustBe None
    }
  }
}
