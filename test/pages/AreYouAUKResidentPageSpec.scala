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

class AreYouAUKResidentPageSpec extends PageBehaviours {

  "AreYouAUKResidentPage" - {

    beRetrievable[Boolean](AreYouAUKResidentPage)

    beSettable[Boolean](AreYouAUKResidentPage)

    beRemovable[Boolean](AreYouAUKResidentPage)

    "must navigate correctly in NormalMode" - {

      "to UKAddressPage when Yes selected" in {
        val ua     = emptyUserAnswers
          .set(
            AreYouAUKResidentPage,
            true
          )
          .success
          .value
        val result = AreYouAUKResidentPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/uk-address")
      }

      "to InternationalAddressPage when no selected" in {
        val ua     = emptyUserAnswers
          .set(
            AreYouAUKResidentPage,
            false
          )
          .success
          .value
        val result = AreYouAUKResidentPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/international-address")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = AreYouAUKResidentPage.navigate(NormalMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to uk address page when yes selected" in {
        val ua     = emptyUserAnswers
          .set(
            AreYouAUKResidentPage,
            true
          )
          .success
          .value
        val result = AreYouAUKResidentPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/change-uk-address")
      }

      "to international address page when no selected" in {
        val ua     = emptyUserAnswers
          .set(
            AreYouAUKResidentPage,
            false
          )
          .success
          .value
        val result = AreYouAUKResidentPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/change-international-address")
      }

      "to JourneyRecovery when not selected" in {
        val ua     = emptyUserAnswers
        val result = AreYouAUKResidentPage.navigate(CheckMode, ua).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "cleanup" - {

      "must cleanup correctly when answered no" in {
        val ua = emptyUserAnswers
          .set(
            UkAddressPage,
            arbitraryUkAddress.arbitrary.sample.value
          )
          .success
          .value

        val cleanedUserAnswers = AreYouAUKResidentPage.cleanup(Some(false), ua).success.value
        cleanedUserAnswers.get(UkAddressPage) mustBe None
      }

      "must cleanup correctly when answered yes" in {
        val ua = emptyUserAnswers
          .set(
            InternationalAddressPage,
            arbitraryInternationalAddress.arbitrary.sample.value
          )
          .success
          .value

        val cleanedUserAnswers = AreYouAUKResidentPage.cleanup(Some(true), ua).success.value
        cleanedUserAnswers.get(InternationalAddressPage) mustBe None
      }
    }
  }
}
