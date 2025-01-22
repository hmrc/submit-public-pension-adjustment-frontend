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

package pages.navigationObjects

import base.SpecBase
import models.calculation.inputs._
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{CheckMode, NormalMode, StatusOfUser}
import org.mockito.MockitoSugar.mock
import pages.{PageBehaviours, PensionSchemeMemberUKAddressPage, StatusOfUserPage}

class ClaimOnBehalfPostALFNavigationSpec extends PageBehaviours with SpecBase {

  val navigationObject = ClaimOnBehalfPostALFNavigation

  val mockCalculationInputsWithLTAOnly =
    CalculationInputs(mock[Resubmission], mock[Setup], None, Some(mock[LifeTimeAllowance]))
  val mockCalculationInputsWithAA      =
    CalculationInputs(mock[Resubmission], mock[Setup], Some(mock[AnnualAllowance]), None)
  val debitPeriodSubmission            =
    Submission(id, uniqueId, calculationInputs, Some(aCalculationResponseWithAnInDateDebitYear))

  "must navigate to alternative name page when LegalPersonalRepresentative answered" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must navigate to alternative name page when LegalPersonalRepresentative answered when LTA only" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      None
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to enter who will pay page when user doesn't select LegalPersonalRepresentative and user is in debit in normal mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/submission-service/2020/who-will-pay-new-tax-charge")
  }

  "must redirect to journey recover when has in date debit but no listed debit periods in submission in normal mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to enter alternate name page when user doesn't select LegalPersonalRepresentative and is in credit/direct comp/indirect comp in normal mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to journey recovery when user doesn't select LegalPersonalRepresentative,  AA submission and no submission calculation in normal mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to journey recovery when user doesn't select LegalPersonalRepresentative, AA submission and no submission calculation in check mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission =
      Submission("id", "submissionUniqueId", mockCalculationInputsWithAA, None)

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to alternate name page when LTA only submission and when user doesn't select LegalPersonalRepresentative, in normal mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/submission-service/name-pension-scheme-holds")
  }

  "must redirect to check your answers when LTA only submission and when user doesn't select LegalPersonalRepresentative, in check mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to check your answers when LTA only submission and when user selects LegalPersonalRepresentative, in check mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      None
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to journey recover when LTA only submission and when user doesn't select LegalPersonalRepresentative, in Normal mode" in {

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to journey recover when LTA only submission and no answer in Check mode" in {

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithLTAOnly,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to who-will-pay-new-tax-charge when user doesn't select LegalPersonalRepresentative,when answered in check mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/submission-service/2020/change-who-will-pay-new-tax-charge")
  }

  "must redirect to CYA page when user selects LegalPersonalRepresentative when answered in check mode" in {

    val userAnswers = emptyUserAnswers
      .set(PensionSchemeMemberUKAddressPage, arbitraryUkAddress.arbitrary.sample.value)
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

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, NormalMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val userAnswers = emptyUserAnswers

    val submission: Submission = Submission(
      "id",
      "submissionUniqueId",
      mockCalculationInputsWithAA,
      Some(aCalculationResponseWithAnInDateDebitYear)
    )

    val nextPageUrl: String = navigationObject.navigate(userAnswers, submission, CheckMode).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
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
