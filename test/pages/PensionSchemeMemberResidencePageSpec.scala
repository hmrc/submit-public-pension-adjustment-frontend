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

import models.{CheckMode, NormalMode}

class PensionSchemeMemberResidencePageSpec extends PageBehaviours {

  "PensionSchemeMemberResidencePage" - {

    beRetrievable[Boolean](PensionSchemeMemberResidencePage)

    beSettable[Boolean](PensionSchemeMemberResidencePage)

    beRemovable[Boolean](PensionSchemeMemberResidencePage)
  }

  "must redirect to enter their uk address page when user selects yes" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/address-someone-else")
  }

  "must redirect to enter their international address page when user selects no" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/international-address-someone-else")
  }

  "must redirect to enter their uk address page when user selects yes in check mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-address-someone-else")
  }

  "must redirect to enter their international address page when user selects no in check mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-international-address-someone-else")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "cleanup" - {

    "must cleanup correctly when answered no" in {
      val ua = emptyUserAnswers
        .set(
          PensionSchemeMemberUKAddressPage,
          arbitraryPensionSchemeMemberUKAddress.arbitrary.sample.value
        )
        .success
        .value

      val cleanedUserAnswers = PensionSchemeMemberResidencePage.cleanup(Some(false), ua).success.value
      cleanedUserAnswers.get(PensionSchemeMemberUKAddressPage) mustBe None
    }

    "must cleanup correctly when answered yes" in {
      val ua = emptyUserAnswers
        .set(
          PensionSchemeMemberInternationalAddressPage,
          arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value
        )
        .success
        .value

      val cleanedUserAnswers = PensionSchemeMemberResidencePage.cleanup(Some(true), ua).success.value
      cleanedUserAnswers.get(PensionSchemeMemberInternationalAddressPage) mustBe None
    }
  }
}
