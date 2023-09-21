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

import models.{CheckMode, NormalMode, PensionSchemeMemberInternationalAddress}

class PensionSchemeMemberInternationalAddressPageSpec extends PageBehaviours {

  "PensionSchemeMemberInternationalAddressPage" - {

    beRetrievable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)

    beSettable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)

    beRemovable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)
  }

  "must navigate to alternative name page when answered" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value

    val result = page.navigate(NormalMode, userAnswers).url

    checkNavigation(result, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to CYA page when answered in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value

    val result = page.navigate(CheckMode, userAnswers).url

    checkNavigation(result, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
