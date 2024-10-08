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

import models.calculation.inputs._
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode, PensionSchemeDetails, PensionSchemeMemberInternationalAddress, Period, StatusOfUser, WhoWillPay}
import org.mockito.MockitoSugar.mock

import java.time.LocalDate

class PensionSchemeMemberInternationalAddressPageSpec extends PageBehaviours {

  "PensionSchemeMemberInternationalAddressPage" - {

    beRetrievable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)

    beSettable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)

    beRemovable[PensionSchemeMemberInternationalAddress](PensionSchemeMemberInternationalAddressPage)
  }

  val mockCalculationInputsWithLTAOnly =
    CalculationInputs(mock[Resubmission], mock[Setup], None, Some(mock[LifeTimeAllowance]))
  val mockCalculationInputsWithAA      =
    CalculationInputs(mock[Resubmission], mock[Setup], Some(mock[AnnualAllowance]), None)
  val debitPeriodSubmission            =
    Submission(id, uniqueId, calculationInputs, Some(aCalculationResponseWithAnInDateDebitYear))

  "must navigate to alternative name page when LegalPersonalRepresentative answered" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val result = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(result, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to enter who will pay page when user doesn't select LegalPersonalRepresentative and user is in debit in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
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

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
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

  "must redirect to enter alternate name page when user doesn't select LegalPersonalRepresentative and is in credit/direct comp/indirect comp in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
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

  "must redirect to journey recovery when user doesn't select LegalPersonalRepresentative,  AA submission and no submission calculation in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to journey recovery when user doesn't select LegalPersonalRepresentative, AA submission and no submission calculation in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to alternate name page when LTA only submission and when user doesn't select LegalPersonalRepresentative, in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
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

  "must redirect to check your answers when LTA only submission and when user doesn't select LegalPersonalRepresentative, in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
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

  "must redirect to journey recover when LTA only submission and when user doesn't select LegalPersonalRepresentative, in Normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

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

    val page = PensionSchemeMemberInternationalAddressPage

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

  "must redirect to who-will-pay-new-tax-charge when user doesn't select LegalPersonalRepresentative,when answered in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val result = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(result, "/submission-service/2020/change-who-will-pay-new-tax-charge")
  }

  "must redirect to CYA page when user selects LegalPersonalRepresentative when answered in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers
      .set(page, arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val result = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(result, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = PensionSchemeMemberInternationalAddressPage

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "cleanup" - {
    "must cleanup correctly when status of user is LegalPersonalRepresentative" in {
      val ua = emptyUserAnswers
        .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
        .success
        .value
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

      val cleanedUserAnswers = PensionSchemeMemberInternationalAddressPage
        .cleanup(Some(arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value), ua)
        .success
        .value

      cleanedUserAnswers.get(WhoWillPayPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(WhoWillPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2021)) mustBe None
      cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2021)) mustBe None
      cleanedUserAnswers.get(SchemeElectionConsentPage(Period._2021)) mustBe None

    }

    "must not cleanup when status of user is not LegalPersonalRepresentative" in {
      val ua = emptyUserAnswers
        .set(StatusOfUserPage, StatusOfUser.Deputyship)
        .success
        .value
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

      val cleanedUserAnswers = PensionSchemeMemberInternationalAddressPage
        .cleanup(Some(arbitraryPensionSchemeMemberInternationalAddress.arbitrary.sample.value), ua)
        .success
        .value

      cleanedUserAnswers.get(WhoWillPayPage(Period._2020)) mustBe Some(WhoWillPay.You)
      cleanedUserAnswers.get(WhoWillPayPage(Period._2021)) mustBe Some(WhoWillPay.PensionScheme)
      cleanedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2021)) mustBe Some("Private pension scheme")
      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2021)) mustBe Some(PensionSchemeDetails("name", "pstr"))
      cleanedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2021)) mustBe Some(true)
      cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2021)) mustBe Some(LocalDate.of(2020, 1, 1))
      cleanedUserAnswers.get(SchemeElectionConsentPage(Period._2021)) mustBe Some(true)

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
