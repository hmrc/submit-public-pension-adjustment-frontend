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
import pages.behaviours.PageBehaviours

class ClaimingHigherOrAdditionalTaxRateReliefPageSpec extends PageBehaviours {

  "ClaimingHigherOrAdditionalTaxRateReliefPage" - {

    beRetrievable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beSettable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    beRemovable[Boolean](ClaimingHigherOrAdditionalTaxRateReliefPage)

    "must navigate correctly in NormalMode" - {

      "to CYA when answered no" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to HowMuchTaxReliefPage when answered yes" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/howMuchTaxRelief")
      }

      "to JourneyRecovery when not answered" in {
        val ua     = emptyUserAnswers
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when answered yes" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            true
          )
          .success
          .value
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to CYA when answered no" in {
        val ua     = emptyUserAnswers
          .set(
            ClaimingHigherOrAdditionalTaxRateReliefPage,
            false
          )
          .success
          .value
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = ClaimingHigherOrAdditionalTaxRateReliefPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
