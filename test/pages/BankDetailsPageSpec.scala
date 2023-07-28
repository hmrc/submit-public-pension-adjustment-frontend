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

import models.{BankDetails, CheckMode, NormalMode}

class BankDetailsPageSpec extends PageBehaviours {

  "BankDetailsPage" - {

    beRetrievable[BankDetails](BankDetailsPage)

    beSettable[BankDetails](BankDetailsPage)

    beRemovable[BankDetails](BankDetailsPage)

    "must navigate correctly in NormalMode" - {

      "to CYA when Pension scheme b scheme selected" in {
        val ua     = emptyUserAnswers
          .set(
            BankDetailsPage,
            BankDetails("Testuser One", "111111", "11111111")
          )
          .success
          .value
        val result = BankDetailsPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/declarations")
      }

      "to JourneyRecoveryPage when not selected" in {
        val ua     = emptyUserAnswers
        val result = BankDetailsPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA" in {
        val ua     = emptyUserAnswers
          .set(
            BankDetailsPage,
            BankDetails("Testuser One", "111111", "11111111")
          )
          .success
          .value
        val result = BankDetailsPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = BankDetailsPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
