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
import uk.gov.hmrc.domain.Nino

class PensionSchemeMemberNinoPageSpec extends PageBehaviours {

  "PensionSchemeMemberNinoPage" - {

    beRetrievable[Nino](PensionSchemeMemberNinoPage)

    beSettable[Nino](PensionSchemeMemberNinoPage)

    beRemovable[Nino](PensionSchemeMemberNinoPage)
  }
  "must redirect to Pension Scheme Members Tax Reference page when user submits data in normal mode" in {

    val page = PensionSchemeMemberNinoPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryNino.arbitrary.sample.value)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/their-UTR")
  }

  "must redirect to cPension Scheme Members Tax Reference page when user submits data in check mode" in {

    val page = PensionSchemeMemberNinoPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryNino.arbitrary.sample.value)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/change-their-UTR")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = PensionSchemeMemberNinoPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode " in {

    val page = PensionSchemeMemberNinoPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
