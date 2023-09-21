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

import models.{CheckMode, NormalMode, PensionSchemeDetails, Period}

class PensionSchemeDetailsPageSpec extends PageBehaviours {

  "PensionSchemeDetailsPage" - {

    beRetrievable[PensionSchemeDetails](PensionSchemeDetailsPage(Period._2020))

    beSettable[PensionSchemeDetails](PensionSchemeDetailsPage(Period._2020))

    beRemovable[PensionSchemeDetails](PensionSchemeDetailsPage(Period._2020))

    "must navigate correctly in NormalMode" - {

      "to AskedPensionSchemeToPayTaxChargePage when questions answered" in {
        val ua     = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage(Period._2020),
            PensionSchemeDetails("name", "pstr")
          )
          .success
          .value
        val result = PensionSchemeDetailsPage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/submission-service/2020/asked-pension-scheme-to-pay-tax-charge")
      }

      "to JourneyRecoveryPage when not answered" in {
        val ua = emptyUserAnswers

        val result = PensionSchemeDetailsPage(Period._2020).navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to asked pension scheme to pay charge page when answered" in {
        val ua     = emptyUserAnswers
          .set(
            PensionSchemeDetailsPage(Period._2020),
            PensionSchemeDetails("name", "pstr")
          )
          .success
          .value
        val result = PensionSchemeDetailsPage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/submission-service/2020/change-asked-pension-scheme-to-pay-tax-charge")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = PensionSchemeDetailsPage(Period._2020).navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
