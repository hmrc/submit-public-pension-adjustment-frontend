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

package controllers

import base.SpecBase
import models.calculation.inputs.CalculationInputs
import models.submission.Submission
import models.{Done, NavigationState, PensionSchemeDetails, Period, UserAnswers, WhenWillYouAskPensionSchemeToPay, WhoWillPay}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.MockitoSugar.mock
import pages._
import play.api.Application
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubmissionDataService
import services.UserDataService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.hmrcfrontend.controllers.routes
import viewmodels.checkAnswers._
import viewmodels.govuk.SummaryListFluency
import views.html.CheckYourAnswersView

import java.time.LocalDate
import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with SummaryListFluency {

  lazy val checkYourAnswerRoute         = controllers.routes.CheckYourAnswersController.onPageLoad.url
  lazy val declarationRoute             = controllers.routes.DeclarationsController.onPageLoad
  lazy val calculationPrerequisiteRoute = controllers.routes.CalculationPrerequisiteController.onPageLoad().url

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val answers        = emptyUserAnswers
      val answersWithNav = NavigationState.save(answers, NavigationState.checkYourAnswersUrl)
      val application    = applicationBuilder(userAnswers = Some(answersWithNav), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, checkYourAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]
        val list = SummaryListViewModel(Seq.empty)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, declarationRoute)(request, messages(application)).toString()
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, checkYourAnswerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, controllers.routes.CheckYourAnswersController.onPageLoad.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must return OK and the correct view for a GET when user enters period based answers" in {

      val mockCalculationInputs     = mock[CalculationInputs]
      val mockUserDataService       = mock[UserDataService]
      val mockSubmissionDataService = mock[SubmissionDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      when(mockCalculationInputs.annualAllowance) thenReturn None
      when(mockCalculationInputs.lifeTimeAllowance) thenReturn None

      val submission: Submission =
        Submission(
          "id",
          "uniqueId",
          mockCalculationInputs,
          Some(aCalculationResponseWithAnInDateDebitYear)
        )

      val ua: UserAnswers = completeUserAnswers
      val answersWithNav  = NavigationState.save(ua, NavigationState.checkYourAnswersUrl)

      val application =
        applicationBuilder(userAnswers = Some(answersWithNav), submission = Some(submission))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, checkYourAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[CheckYourAnswersView]

        val expectedSeq: Seq[SummaryListRow] = expectedSummaryRowsForCompleteAnswers(ua, application)

        val list = SummaryListViewModel(expectedSeq)

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(list, declarationRoute)(request, messages(application)).toString()
      }
    }
  }

  private def expectedSummaryRowsForCompleteAnswers(ua: UserAnswers, application: Application) = {
    val expectedSeq = Seq(
      ClaimOnBehalfSummary.row(ua)(messages(application)) ++
        PeriodDetailsSummary.row(Period._2020)(messages(application)) ++
        WhoWillPaySummary.row(ua, Period._2020)(messages(application)) ++

        PeriodDetailsSummary.row(Period._2021)(messages(application)) ++
        WhoWillPaySummary.row(ua, Period._2021)(messages(application)) ++
        WhichPensionSchemeWillPaySummary.row(ua, Period._2021)(messages(application)) ++
        PensionSchemeDetailsSummary.row(ua, Period._2021)(messages(application)) ++
        AskedPensionSchemeToPayTaxChargeSummary.row(ua, Period._2021)(messages(application)) ++
        WhenDidYouAskPensionSchemeToPaySummary.row(ua, Period._2021)(messages(application)) ++

        PeriodDetailsSummary.row(Period._2022)(messages(application)) ++
        WhoWillPaySummary.row(ua, Period._2022)(messages(application)) ++
        WhichPensionSchemeWillPaySummary.row(ua, Period._2022)(messages(application)) ++
        AskedPensionSchemeToPayTaxChargeSummary.row(ua, Period._2022)(messages(application)) ++
        WhenWillYouAskPensionSchemeToPaySummary.row(ua, Period._2022)(messages(application))
    ).flatten
    expectedSeq
  }

  private def completeUserAnswers = {
    val ua = UserAnswers(userAnswersId)
      .set(ClaimOnBehalfPage, false)
      .success
      .value
      .set(WhoWillPayPage(Period._2020), WhoWillPay.You)
      .success
      .value
      .set(WhoWillPayPage(Period._2021), WhoWillPay.PensionScheme)
      .success
      .value
      .set(WhichPensionSchemeWillPayPage(Period._2021), "Private pension scheme")
      .success
      .value
      .set(PensionSchemeDetailsPage(Period._2021), PensionSchemeDetails("name", "pstr"))
      .success
      .value
      .set(AskedPensionSchemeToPayTaxChargePage(Period._2021), true)
      .success
      .value
      .set(WhenDidYouAskPensionSchemeToPayPage(Period._2021), LocalDate.of(2020, 1, 1))
      .success
      .value
      .set(WhoWillPayPage(Period._2022), WhoWillPay.PensionScheme)
      .success
      .value
      .set(WhichPensionSchemeWillPayPage(Period._2022), "Scheme1_PSTR")
      .success
      .value
      .set(AskedPensionSchemeToPayTaxChargePage(Period._2022), false)
      .success
      .value
      .set(WhenWillYouAskPensionSchemeToPayPage(Period._2022), WhenWillYouAskPensionSchemeToPay.OctToDec23)
      .success
      .value
    ua
  }
}
