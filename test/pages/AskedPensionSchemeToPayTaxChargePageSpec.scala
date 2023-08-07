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

import models.{CheckMode, NormalMode, Period}

class AskedPensionSchemeToPayTaxChargePageSpec extends PageBehaviours {

  "AskedPensionSchemeToPayTaxChargePage" - {

    beRetrievable[Boolean](AskedPensionSchemeToPayTaxChargePage(Period._2020))

    beSettable[Boolean](AskedPensionSchemeToPayTaxChargePage(Period._2020))

    beRemovable[Boolean](AskedPensionSchemeToPayTaxChargePage(Period._2020))

    "must navigate correctly in NormalMode" - {

      "to WhenDidYouAskPensionSchemeToPayPage when Yes selected" in {
        val ua     = emptyUserAnswers
          .set(
            AskedPensionSchemeToPayTaxChargePage(Period._2020),
            true
          )
          .success
          .value
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/when-did-you-ask-pension-scheme-to-pay/2020")
      }

      "to WhenWillYouAskPensionSchemeToPayPage when no selected" in {
        val ua     = emptyUserAnswers
          .set(
            AskedPensionSchemeToPayTaxChargePage(Period._2020),
            false
          )
          .success
          .value
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/when-will-you-ask-pension-scheme-to-pay/2020")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to when did you ask pension scheme to charge page in check mode when yes selected" in {
        val ua     = emptyUserAnswers
          .set(
            AskedPensionSchemeToPayTaxChargePage(Period._2020),
            true
          )
          .success
          .value
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/change-when-did-you-ask-pension-scheme-to-pay/2020")
      }

      "to when will you ask pension scheme to charge page in check mode when no selected" in {
        val ua     = emptyUserAnswers
          .set(
            AskedPensionSchemeToPayTaxChargePage(Period._2020),
            false
          )
          .success
          .value
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/change-when-will-you-ask-pension-scheme-to-pay/2020")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
