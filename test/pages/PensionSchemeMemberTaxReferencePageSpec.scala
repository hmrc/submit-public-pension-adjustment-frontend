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

import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow}

class PensionSchemeMemberTaxReferencePageSpec extends PageBehaviours {

  "PensionSchemeMemberTaxReferencePage" - {

    beRetrievable[String](PensionSchemeMemberTaxReferencePage)

    beSettable[String](PensionSchemeMemberTaxReferencePage)

    beRemovable[String](PensionSchemeMemberTaxReferencePage)
  }

  "must redirect to alf when user submits data in normal mode" in {

    val page = PensionSchemeMemberTaxReferencePage

    val userAnswers = emptyUserAnswers
      .set(page, "1234567890")
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/alf-ramp-on-claim-on-behalf/normal-mode")
  }

  "must redirect to alf when user submits data in check mode" in {

    val page = PensionSchemeMemberTaxReferencePage

    val userAnswers = emptyUserAnswers
      .set(page, "1234567890")
      .get
      .set(RunThroughOnBehalfFlow(), true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/alf-ramp-on-claim-on-behalf/check-mode")
  }

  "must redirect to check your answers when RunThroughOnBehalfFlow is false or empty in check mode" in {

    val page = PensionSchemeMemberTaxReferencePage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }
}
