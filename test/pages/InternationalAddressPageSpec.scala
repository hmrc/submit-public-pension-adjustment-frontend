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

import models.calculation.response.TaxYearScheme
import models.submission.Submission
import models.{CheckMode, InternationalAddress, NormalMode}

class InternationalAddressPageSpec extends PageBehaviours {

  "InternationalAddressPage" - {

    beRetrievable[InternationalAddress](InternationalAddressPage)

    beSettable[InternationalAddress](InternationalAddressPage)

    beRemovable[InternationalAddress](InternationalAddressPage)

    "must navigate correctly in NormalMode" - {

      "to LegacyPensionSchemeReferencePage when answered" in {

        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
          .set(
            InternationalAddressPage,
            arbitraryInternationalAddress.arbitrary.sample.value
          )
          .success
          .value
        val result = InternationalAddressPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/12345678AB/legacy-individual-pension-scheme-reference")
      }

      "to JourneyRecovery when not answered" in {
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
        val result = InternationalAddressPage.navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when answered" in {
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
          .set(
            InternationalAddressPage,
            arbitraryInternationalAddress.arbitrary.sample.value
          )
          .success
          .value
        val result = InternationalAddressPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
        val result = InternationalAddressPage.navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
