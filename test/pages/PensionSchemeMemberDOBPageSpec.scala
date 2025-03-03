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
import models.{CheckMode, NormalMode, Period, RunThroughOnBehalfFlow, StatusOfUser, WhoWillPay}
import org.scalacheck.Arbitrary

import java.time.LocalDate

class PensionSchemeMemberDOBPageSpec extends PageBehaviours {

  "PensionSchemeMemberDOBPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](PensionSchemeMemberDOBPage)

    beSettable[LocalDate](PensionSchemeMemberDOBPage)

    beRemovable[LocalDate](PensionSchemeMemberDOBPage)
  }

  "normal mode" - {

    "must redirect to Pension Scheme Members Nino page when user submits data and has selected power of attorney" in {

      val page = PensionSchemeMemberDOBPage

      val userAnswers = emptyUserAnswers
        .set(page, LocalDate.of(1995, 1, 1))
        .success
        .value
        .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/submission-service/national-insurance-number-someone-else")

    }

    "must redirect to Pension Scheme Members Nino page when user submits data and has selected deputyship" in {

      val page = PensionSchemeMemberDOBPage

      val userAnswers = emptyUserAnswers
        .set(StatusOfUserPage, StatusOfUser.Deputyship)
        .success
        .value
        .set(page, LocalDate.of(1995, 1, 1))
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/submission-service/national-insurance-number-someone-else")

    }

    "must redirect to member date of death page when user submits data and has selected legal personal representative" in {

      val page = PensionSchemeMemberDOBPage

      val userAnswers = emptyUserAnswers
        .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
        .success
        .value
        .set(page, LocalDate.of(1995, 1, 1))
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/submission-service/date-of-death-someone-else")

    }

    "must redirect to journey recovery when not answered" in {

      val page = PensionSchemeMemberDOBPage

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "when run through on behalf flow" - {
      "when status of user == legal personal representative, redirect to member date of death" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(RunThroughOnBehalfFlow(), true)
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
          .success
          .value
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/submission-service/change-date-of-death-someone-else")

      }

      "when status of user NOT legal personal representative, redirect to member NINO page" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(RunThroughOnBehalfFlow(), true)
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.Deputyship)
          .success
          .value
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/submission-service/change-national-insurance-number-someone-else")

      }

      "when no status of user, redirect to journey recovery" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(RunThroughOnBehalfFlow(), true)
          .success
          .value
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/there-is-a-problem")

      }
    }

    "noClaimOnBehalfRunThrough" - {

      "when status of user == legal personal representative and member date of death not defined, redirect to member date of death" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
          .success
          .value

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/submission-service/change-date-of-death-someone-else")

      }

      "when status of user == legal personal representative and member date of death is defined, redirect to CYA" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
          .success
          .value
          .set(MemberDateOfDeathPage, LocalDate.of(2020, 1, 1))
          .success
          .value

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/check-your-answers")

      }

      "when status of user not legal representative must go to AA debit loop in normal mode when has a debit and not answered debit loop" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.Deputyship)
          .success
          .value
          .set(MemberDateOfDeathPage, LocalDate.of(2020, 1, 1))
          .success
          .value

        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
            .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/submission-service/2020/who-will-pay-new-tax-charge")
      }

      "when status of user not legal representative must go to CYA when has a debit and has answered debit loop" in {

        val page = PensionSchemeMemberDOBPage

        val userAnswers = emptyUserAnswers
          .set(page, LocalDate.of(1995, 1, 1))
          .success
          .value
          .set(StatusOfUserPage, StatusOfUser.Deputyship)
          .success
          .value
          .set(MemberDateOfDeathPage, LocalDate.of(2020, 1, 1))
          .success
          .value
          .set(WhoWillPayPage(Period._2020), WhoWillPay.You)
          .success
          .value

        val submission: Submission =
          submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
            .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

        val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

        checkNavigation(nextPageUrl, "/check-your-answers")
      }
    }
  }
}
