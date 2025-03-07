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

package pages.navigationObjects

import base.SpecBase
import models.BankDetails
import pages.{BankDetailsPage, PageBehaviours}

class BankDetailsNavigationSpec extends PageBehaviours with SpecBase {

  val navigationObject = BankDetailsNavigation

  "must navigate correctly in either mode" - {

    "to CYA" in {
      val ua     = emptyUserAnswers
        .set(
          BankDetailsPage,
          BankDetails("Testuser One", "111111", "11111111", None)
        )
        .success
        .value
      val result = navigationObject.navigate(ua).url

      checkNavigation(result, "/check-your-answers")
    }

    "to JourneyRecoveryPage when not answered" in {
      val ua     = emptyUserAnswers
      val result = navigationObject.navigate(ua).url

      checkNavigation(result, "/there-is-a-problem")
    }
  }
}
