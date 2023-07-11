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

import java.time.LocalDate
import org.scalacheck.Arbitrary

class WhenDidYouAskPensionSchemeToPayPageSpec extends PageBehaviours {

  "WhenDidYouAskPensionSchemeToPayPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](WhenDidYouAskPensionSchemeToPayPage)

    beSettable[LocalDate](WhenDidYouAskPensionSchemeToPayPage)

    beRemovable[LocalDate](WhenDidYouAskPensionSchemeToPayPage)
  }

  val validDate = LocalDate.of(2020, 1, 1)

  "must navigate correctly in NormalMode" - {

    "to AlternativeNamePage when user submits" in {
      val ua = emptyUserAnswers
        .set(
          WhenDidYouAskPensionSchemeToPayPage,
          validDate
        )
        .get

      val result = WhenDidYouAskPensionSchemeToPayPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/alternative-name")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result = WhenDidYouAskPensionSchemeToPayPage.navigate(NormalMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

  "must navigate correctly in CheckMode" - {

    "to CYA when selected" in {

      val ua = emptyUserAnswers
        .set(
          WhenDidYouAskPensionSchemeToPayPage,
          validDate
        )
        .get

      val result = WhenDidYouAskPensionSchemeToPayPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/check-your-answers")
    }

    "to JourneyRecovery when not selected" in {
      val ua     = emptyUserAnswers
      val result = WhenDidYouAskPensionSchemeToPayPage.navigate(CheckMode, ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }

}
