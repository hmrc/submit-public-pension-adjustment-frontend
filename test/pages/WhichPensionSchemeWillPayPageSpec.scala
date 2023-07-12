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

import models.{CheckMode, NormalMode, WhichPensionSchemeWillPay}

class WhichPensionSchemeWillPayPageSpec extends PageBehaviours {

  "WhichPensionSchemeWillPayPage" - {

    beRetrievable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    beSettable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    beRemovable[WhichPensionSchemeWillPay](WhichPensionSchemeWillPayPage)

    "must navigate correctly in NormalMode" - {

      "to PensionSchemeDetails when Private pension scheme selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayPage,
            WhichPensionSchemeWillPay.PrivatePensionScheme
          )
          .success
          .value
        val result = WhichPensionSchemeWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/pensionSchemeDetails")
      }

      "to CYA when public Pension scheme selected" in {
        val ua     = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayPage,
            publicPensionScheme.sample.value
          )
          .success
          .value
        val result = WhichPensionSchemeWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/askedPensionSchemeToPayTaxCharge")
      }

      "to JourneyRecoveryPage when not selected" in {
        val ua     = emptyUserAnswers
        val result = WhichPensionSchemeWillPayPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA" in {
        val ua     = emptyUserAnswers
          .set(
            WhichPensionSchemeWillPayPage,
            WhichPensionSchemeWillPay.PensionSchemeA
          )
          .success
          .value
        val result = WhichPensionSchemeWillPayPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = WhichPensionSchemeWillPayPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
