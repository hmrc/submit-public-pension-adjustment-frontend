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

import models.{CheckMode, NormalMode, WhoWillPay}

class WhoWillPayPageSpec extends PageBehaviours {

  "WhoWillPayPage" - {

    beRetrievable[WhoWillPay](WhoWillPayPage)

    beSettable[WhoWillPay](WhoWillPayPage)

    beRemovable[WhoWillPay](WhoWillPayPage)

    "must navigate correctly in NormalMode" - {

      "to WhichPensionSchemeWillPayPage when PensionScheme selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.PensionScheme
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/whichPensionSchemeWillPay")
      }

      "to AlternativeNamePage when You selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.You
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/alternative-name")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = WhoWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.You
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = WhoWillPayPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
