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

import models.calculation.response.{CalculationResponse, Period, TotalAmounts}
import models.submission.Submission
import models.{NormalMode, Period, SchemeCreditConsent}

class SchemeCreditConsentPageSpec extends PageBehaviours {

  "SchemeCreditConsentPage" - {

    beRetrievable[Set[SchemeCreditConsent]](SchemeCreditConsentPage)

    beSettable[Set[SchemeCreditConsent]](SchemeCreditConsentPage)

    beRemovable[Set[SchemeCreditConsent]](SchemeCreditConsentPage)
  }

  "must navigate correctly" - {

    "to declarations when answered" in {

      val userAnswers = emptyUserAnswers
        .set(SchemeCreditConsentPage, SchemeCreditConsent.values.toSet)
        .success
        .value

      val result = SchemeCreditConsentPage.navigate(NormalMode, userAnswers).url

      checkNavigation(result, "/declarations")
    }
  }

  "to journey recovery when not answered" in {

    val userAnswers = emptyUserAnswers

    val result = SchemeCreditConsentPage.navigate(NormalMode, userAnswers).url

    checkNavigation(result, "/there-is-a-problem")
  }
}
