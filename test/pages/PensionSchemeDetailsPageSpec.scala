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

import models.{CheckMode, NormalMode, PensionSchemeDetails}
import pages.behaviours.PageBehaviours

class PensionSchemeDetailsPageSpec extends PageBehaviours {

  "PensionSchemeDetailsPage" - {

    beRetrievable[PensionSchemeDetails](PensionSchemeDetailsPage)

    beSettable[PensionSchemeDetails](PensionSchemeDetailsPage)

    beRemovable[PensionSchemeDetails](PensionSchemeDetailsPage)

    "must navigate correctly in NormalMode" - {

      "to AskedPensionSchemeToPayTaxChargePage when questions answered" in {
        val ua = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage,
            PensionSchemeDetails("name", "pstr")
          )
          .success
          .value
        val result = PensionSchemeDetailsPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/askedPensionSchemeToPayTaxCharge")
      }

      "to JourneyRecoveryPage when not answered" in {
        val ua = emptyUserAnswers

        val result = PensionSchemeDetailsPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when answered" in {
        val ua = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage,
            PensionSchemeDetails("name", "pstr")
          )
          .success
          .value
        val result = PensionSchemeDetailsPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not answered" in {
        val ua = emptyUserAnswers
        val result = PensionSchemeDetailsPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
