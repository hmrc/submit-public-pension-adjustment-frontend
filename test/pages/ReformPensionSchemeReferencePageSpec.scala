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
import models.{CheckMode, NormalMode, PSTR}

class ReformPensionSchemeReferencePageSpec extends PageBehaviours {

  "ReformPensionSchemeReferencePage" - {

    beRetrievable[String](ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"))

    beSettable[String](ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"))

    beRemovable[String](ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"))

    "must navigate correctly in NormalMode" - {

      "to ClaimingHigherOrAdditionalTaxRateReliefPage there are no more schemes" in {

        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
          .set(
            ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"),
            "QT123456123456"
          )
          .success
          .value
        val result =
          ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1").navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/claiming-higher-additional-tax-rate-relief")
      }

      "back to LegacyPensionSchemeReferencePage for next scheme when there are more schemes" in {

        val submission: Submission = submissionRelatingToTaxYearSchemes(
          List(
            TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None),
            TaxYearScheme("scheme2", "12345678AC", 0, 0, 0, None, None)
          )
        )

        val ua     = emptyUserAnswers
          .set(
            ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"),
            "QT123456123456"
          )
          .success
          .value
        val result =
          ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1").navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/submission-service/12345678AC/legacy-individual-pension-scheme-reference")
      }

      "to JourneyRecovery when not answered" in {
        val ua                     = emptyUserAnswers
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))
        val result                 =
          ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1").navigate(NormalMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }

    "must navigate correctly in CheckMode" - {

      "to CYA when answered" in {
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))

        val ua     = emptyUserAnswers
          .set(
            ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"),
            "ripsr"
          )
          .success
          .value
        val result =
          ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1").navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/check-your-answers")
      }

      "to JourneyRecovery when not selected" in {
        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, 0, None, None)))
        val ua                     = emptyUserAnswers
        val result                 =
          ReformPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1").navigate(CheckMode, ua, submission).url

        checkNavigation(result, "/there-is-a-problem")
      }
    }
  }
}
