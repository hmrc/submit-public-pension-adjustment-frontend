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

    checkNavigation(nextPageUrl, "/their-uk-address")
  }

  "must redirect to enter their international address page when user selects no" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/their-international-address")
  }

  "must redirect to enter their uk address page when user selects yes in check mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/change-their-uk-address")
  }

  "must redirect to enter their international address page when user selects no in check mode" in {

    val page = PensionSchemeMemberResidencePage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/change-their-international-address")
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
}
