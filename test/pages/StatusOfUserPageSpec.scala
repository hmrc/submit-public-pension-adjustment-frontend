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

import models.StatusOfUser.{Deputyship, LegalPersonalRepresentative, PowerOfAttorney}
import models.calculation.response.TaxYearScheme
import models.submission.Submission
import models.{CheckMode, NormalMode, PensionSchemeDetails, Period, RunThroughOnBehalfFlow, StatusOfUser, WhenWillYouAskPensionSchemeToPay, WhoWillPay}

import java.time.LocalDate

class StatusOfUserPageSpec extends PageBehaviours {

  val validDate = LocalDate.of(1995, 1, 1)

  "StatusOfUserPage" - {

    beRetrievable[StatusOfUser](StatusOfUserPage)

    beSettable[StatusOfUser](StatusOfUserPage)

    beRemovable[StatusOfUser](StatusOfUserPage)
  }

  "normal mode" - {

    "must redirect to their name page when user submits data" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, Deputyship)
        .success
        .value

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/submission-service/name-someone-else")
    }

    "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = page.navigate(NormalMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }

  "check mode" - {

    "must redirect to pension scheme memmber name page when has run through claim on behalf" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(RunThroughOnBehalfFlow(), true)
        .success
        .value
        .set(page, PowerOfAttorney)
        .success
        .value

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url
      checkNavigation(nextPageUrl, "/submission-service/change-name-someone-else")

    }

    "must redirect to cya when legal personal rep, no debit submission and member date of death already defined" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, LegalPersonalRepresentative)
        .success
        .value
        .set(MemberDateOfDeathPage, LocalDate.of(2020, 1, 1))
        .success
        .value

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url
      checkNavigation(nextPageUrl, "/check-your-answers")
    }

    "must redirect to cya when legal personal rep, has debit submission and member date of death already defined" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, LegalPersonalRepresentative)
        .success
        .value
        .set(MemberDateOfDeathPage, LocalDate.of(2020, 1, 1))
        .success
        .value

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
          .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url
      checkNavigation(nextPageUrl, "/check-your-answers")
    }

    "must redirect to member date of death page when legal personal rep, has debit submission and member date of death not defined" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, LegalPersonalRepresentative)
        .success
        .value

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
          .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url
      checkNavigation(nextPageUrl, "/submission-service/change-date-of-death-someone-else")
    }

    "must redirect to AA Debit loop when deputyship in normal mode, has debit submission and member date of death already defined but no debit answers" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, Deputyship)
        .success
        .value

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))
          .copy(calculation = Some(aCalculationResponseWithAnInDateDebitYear))

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url
      checkNavigation(nextPageUrl, "/submission-service/2020/who-will-pay-new-tax-charge")
    }

    "must redirect to CYA when deputyship, has debit submission and member date of death already defined and has debit answers" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers
        .set(page, Deputyship)
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

    "must redirect to JourneyRecoveryPage when not answered in check mode" in {

      val page = StatusOfUserPage

      val userAnswers = emptyUserAnswers

      val nextPageUrl: String = page.navigate(CheckMode, userAnswers, submission).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }

  }
  "cleanup" - {

    "must cleanup member date of death when user selects PoA" in {

      val ua = emptyUserAnswers
        .set(MemberDateOfDeathPage, validDate)
        .success
        .value

      val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(PowerOfAttorney), ua).success.value

      cleanedUserAnswers.get(MemberDateOfDeathPage) `mustBe` None

    }

    "must not cleanup member date of death when user selects LegalPersonalRepresentative" in {

      val ua = emptyUserAnswers
        .set(MemberDateOfDeathPage, validDate)
        .success
        .value

      val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(LegalPersonalRepresentative), ua).success.value

      cleanedUserAnswers.get(MemberDateOfDeathPage) `mustBe` Some(validDate)

    }

    "must cleanup debit loop when legal personal representative" in {

      val ua = emptyUserAnswers
        .set(
          WhichPensionSchemeWillPayPage(Period._2020),
          "Private pension scheme"
        )
        .success
        .value
        .set(PensionSchemeDetailsPage(Period._2020), PensionSchemeDetails("name", "pstr"))
        .success
        .value
        .set(AskedPensionSchemeToPayTaxChargePage(Period._2020), false)
        .success
        .value
        .set(WhenDidYouAskPensionSchemeToPayPage(Period._2020), LocalDate.of(2020, 1, 1))
        .success
        .value
        .set(WhenWillYouAskPensionSchemeToPayPage(Period._2020), WhenWillYouAskPensionSchemeToPay.OctToDec23)
        .success
        .value
        .set(SchemeElectionConsentPage(Period._2020), true)
        .success
        .value

      val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(LegalPersonalRepresentative), ua).success.value

      cleanedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2020)) mustBe None
      cleanedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(WhenWillYouAskPensionSchemeToPayPage(Period._2020)) mustBe None
      cleanedUserAnswers.get(SchemeElectionConsentPage(Period._2020)) mustBe None

    }

    "must cleanup member date of death when user selects deputyship" in {

      val ua = emptyUserAnswers
        .set(MemberDateOfDeathPage, validDate)
        .success
        .value

      val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(Deputyship), ua).success.value

      cleanedUserAnswers.get(MemberDateOfDeathPage) `mustBe` None

    }
  }
}
