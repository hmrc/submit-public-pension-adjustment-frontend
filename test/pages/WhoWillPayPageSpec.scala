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
import pages.behaviours.PageBehaviours

class WhoWillPayPageSpec extends PageBehaviours {

  "WhoWillPayPage" - {

    beRetrievable[WhoWillPay](WhoWillPayPage)

    beSettable[WhoWillPay](WhoWillPayPage)

    beRemovable[WhoWillPay](WhoWillPayPage)

    "must navigate correctly in NormalMode" - {

      "to WhichPensionSchemeWillPayPage when PensionScheme selected" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.Pensionscheme
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(NormalMode, ua).url

        result mustBe "/whichPensionSchemeWillPay"
      }

      "to CYA when You selected" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.You
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(NormalMode, ua).url

        result mustBe "/check-your-answers"
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA" in {
        val ua = emptyUserAnswers
          .set(
            WhoWillPayPage,
            WhoWillPay.You
          )
          .success
          .value
        val result = WhoWillPayPage.navigate(CheckMode, ua).url

        result mustBe "/check-your-answers"
      }
    }
  }
}
