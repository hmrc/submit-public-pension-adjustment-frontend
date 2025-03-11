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

import models.calculation.inputs.{CalculationInputs, LifeTimeAllowance, Resubmission, Setup}
import models.calculation.response.TaxYearScheme
import models.submission.Submission
import models.{CheckMode, NormalMode, Period, RunThroughOnBehalfFlow, StatusOfUser, WhoWillPay}
import org.mockito.MockitoSugar.mock
import org.scalacheck.Arbitrary

import java.time.LocalDate

class MemberDateOfDeathPageSpec extends PageBehaviours {

  val mockCalculationInputsWithLTAOnly =
    CalculationInputs(mock[Resubmission], mock[Setup], None, Some(mock[LifeTimeAllowance]))

  "MemberDateOfDeathPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](MemberDateOfDeathPage)

    beSettable[LocalDate](MemberDateOfDeathPage)

    beRemovable[LocalDate](MemberDateOfDeathPage)
  }

  "must redirect to Pension Scheme Members Nino page when user submits data in normal mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/national-insurance-number-someone-else")

  }

  "must redirect to journey recovery when no data submitted in normal mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to Pension Scheme Members Nino page when user submits data in checkmode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .get
      .set(RunThroughOnBehalfFlow(), true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/change-national-insurance-number-someone-else")

  }

  "when no run through claim on behalf" - {

    "must redirect to CYA when status of user Legal Personal Rep in check mode" in {

      val page = MemberDateOfDeathPage

      val userAnswers = emptyUserAnswers
        .set(page, LocalDate.of(1995, 1, 1))
        .get
        .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
        .get

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/check-your-answers")
    }

    "must redirect to AA Debit loop when user has not answered debit loop previously and has debit and not legal representative in normal mode" in {

      val page = MemberDateOfDeathPage

      val userAnswers = emptyUserAnswers
        .set(page, LocalDate.of(1995, 1, 1))
        .get
        .set(StatusOfUserPage, StatusOfUser.Deputyship)
        .get

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
          .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/submission-service/2020/who-will-pay-new-tax-charge")
    }

    "must redirect to CYA when user has answered debit loop previously and has debit and not legal representative in normal mode" in {

      val page = MemberDateOfDeathPage

      val userAnswers = emptyUserAnswers
        .set(page, LocalDate.of(1995, 1, 1))
        .get
        .set(StatusOfUserPage, StatusOfUser.Deputyship)
        .get
        .set(WhoWillPayPage(Period._2020), WhoWillPay.You)
        .get

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
          .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/check-your-answers")
    }
  }

  "must redirect to CYA when user is legal representative with debit in check mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .get
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .get

    val submission: Submission =
      submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
        .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to journey recover when no status of user" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .get

    val submission: Submission =
      submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
        .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")

  }
}
