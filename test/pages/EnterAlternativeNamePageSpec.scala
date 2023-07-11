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
import pages.behaviours.PageBehaviours

class EnterAlternativeNamePageSpec extends PageBehaviours {

  "EnterAlternativeNamePage" - {

    beRetrievable[String](EnterAlternativeNamePage)

    beSettable[String](EnterAlternativeNamePage)

    beRemovable[String](EnterAlternativeNamePage)
  }

  "must redirect to contact number when user submits answers" in {

    val page = EnterAlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, "John Doe")
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/contact-number")
  }

  "must redirect to check your answers page when user submits answers in check mode" in {

    val page = EnterAlternativeNamePage

    val userAnswers = emptyUserAnswers
      .set(page, "John Doe")
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode " in {

    val page = EnterAlternativeNamePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode " in {

    val page = EnterAlternativeNamePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
