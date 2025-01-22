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

import models.StatusOfUser.Deputyship
import models.calculation.inputs._
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, WhoWillPay}
import org.mockito.MockitoSugar.mock

import java.time.LocalDate

class ClaimOnBehalfPageSpec extends PageBehaviours {

  "ClaimOnBehalfPage" - {

    beRetrievable[Boolean](ClaimOnBehalfPage)

    beSettable[Boolean](ClaimOnBehalfPage)

    beRemovable[Boolean](ClaimOnBehalfPage)
  }

  val mockCalculationInputsWithLTAOnly =
    CalculationInputs(mock[Resubmission], mock[Setup], None, Some(mock[LifeTimeAllowance]))
  val mockCalculationInputsWithAA      =
    CalculationInputs(mock[Resubmission], mock[Setup], Some(mock[AnnualAllowance]), None)

  val debitPeriodSubmission =
    Submission(id, uniqueId, calculationInputs, Some(aCalculationResponseWithAnInDateDebitYear))

  "must redirect to status of user page when user selects yes in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)
    val nextPageUrl: String    = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/authority-someone-else")
  }

  "must redirect to enter who will pay page when user selects no and user is in debit in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/2020/who-will-pay-new-tax-charge")
  }

  "must redirect to journey recover when has in date debit but no listed debit periods in submission in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithDebitButNoPeriods)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to enter alternate name page when user selects no and is in credit/direct comp/indirect comp in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val calculationResponse    = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(1, 0, 0),
      List.empty,
      List.empty
    )
    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to journey recovery when AA submission and no submission calculation in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to journey recovery when AA submission and no submission calculation in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to set status of user when user submits yes in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/change-authority-someone-else")
  }

  "must redirect to who paid charge page when user selects no and user is in debit in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/2020/change-who-will-pay-new-tax-charge")
  }

  "must redirect to check your answers page when user selects no and is in credit/direct comp/indirect comp " in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(1, 0, 0),
      List.empty,
      List.empty
    )

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to alternate name page when LTA only submission and user selects no in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to status of user page when LTA only submission and user selects yes in normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/authority-someone-else")
  }

  "must redirect to check your answers when LTA only submission and user selects no in check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, false)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to change status of user when LTA only submission and user selects yes in Normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/authority-someone-else")
  }

  "must redirect to change status of user when LTA only submission and user selects yes in Check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers
      .set(page, true)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/submission-service/change-authority-someone-else")
  }

  "must redirect to journey recover when LTA only submission and no answer in Normal mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to journey recover when LTA only submission and no answer in Check mode" in {

    val page = ClaimOnBehalfPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "cleanup" - {

    "must cleanup correctly when answered no" in {
      val ua = emptyUserAnswers
        .set(
          StatusOfUserPage,
          Deputyship
        )
        .success
        .value
        .set(
          PensionSchemeMemberNamePage,
          "John Doe"
        )
        .success
        .value
        .set(
          PensionSchemeMemberDOBPage,
          LocalDate.of(1995, 1, 1)
        )
        .success
        .value
        .set(
          MemberDateOfDeathPage,
          LocalDate.of(2022, 1, 1)
        )
        .success
        .value
        .set(
          PensionSchemeMemberNinoPage,
          arbitraryNino.arbitrary.sample.value
        )
        .success
        .value
        .set(
          PensionSchemeMemberTaxReferencePage,
          "1234567890"
        )
        .success
        .value
        .set(PensionSchemeMemberResidencePage, true)
        .success
        .value
        .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
        .success
        .value

      val cleanedUserAnswers = ClaimOnBehalfPage.cleanup(Some(false), ua).success.value

      cleanedUserAnswers.get(StatusOfUserPage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberNamePage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberDOBPage) mustBe None
      cleanedUserAnswers.get(MemberDateOfDeathPage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberNinoPage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberTaxReferencePage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberResidencePage) mustBe None
      cleanedUserAnswers.get(PensionSchemeMemberUKAddressPage) mustBe None
    }

    "must cleanup correctly when answered yes" in {
      val ua = emptyUserAnswers
        .set(
          WhoWillPayPage(Period._2020),
          WhoWillPay.You
        )
        .success
        .value
        .set(
          WhoWillPayPage(Period._2021),
          WhoWillPay.PensionScheme
        )
        .success
        .value
        .set(
          WhichPensionSchemeWillPayPage(Period._2021),
          "Private pension scheme"
        )
        .success
        .value
        .set(
          PensionSchemeDetailsPage(Period._2021),
          PensionSchemeDetails("name", "pstr")
        )
        .success
        .value
        .set(
          AskedPensionSchemeToPayTaxChargePage(Period._2021),
          true
        )
        .success
        .value
        .set(
          WhenDidYouAskPensionSchemeToPayPage(Period._2021),
          LocalDate.of(2020, 1, 1)
        )
        .success
        .value
        .set(
          SchemeElectionConsentPage(Period._2021),
          true
        )
        .success
        .value

      val cleanedUserAnswers = ClaimOnBehalfPage.cleanup(Some(true), ua).success.value

      cleanedUserAnswers.get(WhoWillPayPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(WhoWillPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2021)) mustBe None
      cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(SchemeElectionConsentPage(Period._2021)) mustBe None

    }
  }

  private def aCalculationResponseWithDebitButNoPeriods = {

    val calculationResponse = CalculationResponse(
      models.calculation.response.Resubmission(false, None),
      TotalAmounts(0, 1, 0),
      List.empty,
      List.empty
    )
    calculationResponse
  }
}
