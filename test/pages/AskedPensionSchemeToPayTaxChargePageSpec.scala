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

import models.{CheckMode, NormalMode, Period, WhenWillYouAskPensionSchemeToPay}

import java.time.LocalDate

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

        checkNavigation(result, "/submission-service/2020/date-asked-pension-scheme-to-pay")
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

        checkNavigation(result, "/submission-service/2020/date-you-will-ask-pension-scheme-to-pay")
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

        checkNavigation(result, "/submission-service/2020/change-date-asked-pension-scheme-to-pay")
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

        checkNavigation(result, "/submission-service/2020/change-date-you-will-ask-pension-scheme-to-pay")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = AskedPensionSchemeToPayTaxChargePage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "must cleanup correctly when answered no" in {
        val ua = emptyUserAnswers
          .set(
            WhenDidYouAskPensionSchemeToPayPage(Period._2020),
            LocalDate.of(2020, 1, 1)
          )
          .success
          .value

        val cleanedUserAnswers =
          AskedPensionSchemeToPayTaxChargePage(Period._2020).cleanup(Some(false), ua).success.value
        cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2020)) mustBe None
      }

      "must cleanup correctly when answered yes" in {
        val ua = emptyUserAnswers
          .set(
            WhenWillYouAskPensionSchemeToPayPage(Period._2020),
            WhenWillYouAskPensionSchemeToPay.OctToDec23
          )
          .success
          .value

        val cleanedUserAnswers =
          AskedPensionSchemeToPayTaxChargePage(Period._2020).cleanup(Some(true), ua).success.value
        cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2020)) mustBe None
      }
    }
  }
}
