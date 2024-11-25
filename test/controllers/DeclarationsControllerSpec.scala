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
import models.{Done, PensionSchemeDetails, Period, UserAnswers, UserSubmissionReference, WhenWillYouAskPensionSchemeToPay, WhoWillPay}
import models.calculation.inputs.{CalculationInputs, Resubmission}
import models.finalsubmission.FinalSubmissionResponse
import models.submission.Submission
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, verifyNoInteractions, when}
import org.mockito.MockitoSugar.mock
import org.mockito.captor.ArgCaptor
import play.api.inject.bind
import pages.{AskedPensionSchemeToPayTaxChargePage, ClaimOnBehalfPage, PensionSchemeDetailsPage, PensionSchemeMemberNamePage, WhenDidYouAskPensionSchemeToPayPage, WhenWillYouAskPensionSchemeToPayPage, WhichPensionSchemeWillPayPage, WhoWillPayPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmissionDataService, SubmissionService, UserDataService}
import views.html.DeclarationsView

import java.time.LocalDate
import scala.concurrent.Future

class DeclarationsControllerSpec extends SpecBase {

  lazy val declarationsRoute            = routes.DeclarationsController.onPageLoad.url
  lazy val submitYourAnswerRoute        = routes.DeclarationsController.onSubmit().url
  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  "Declarations Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, declarationsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclarationsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(false, "")(request, messages(application)).toString
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, declarationsRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }

    "must return isClaimOnBehalf true when user answers yes in claim on behalf page " in {

      val ua = emptyUserAnswers.set(ClaimOnBehalfPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(ua), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, declarationsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclarationsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(isClaimOnBehalf = true, "")(request, messages(application)).toString
      }
    }

    "must return isClaimOnBehalf false when user answers no in claim on behalf page " in {

      val ua = emptyUserAnswers.set(ClaimOnBehalfPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(ua), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, declarationsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclarationsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(isClaimOnBehalf = false, "")(request, messages(application)).toString
      }
    }

    "must redirect to submission waiting room controller when submitted " in {

      val ua = emptyUserAnswers.set(ClaimOnBehalfPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(ua), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, submitYourAnswerRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclarationsView]

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SubmissionWaitingRoomController.onPageLoad().url
      }
    }

    "must retrieve Pension Scheme Member Name from user answers" in {

      val ua = emptyUserAnswers
        .set(ClaimOnBehalfPage, true)
        .success
        .value
        .set(PensionSchemeMemberNamePage, "John Doe")
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(ua), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, declarationsRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[DeclarationsView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(isClaimOnBehalf = true, "John Doe")(
          request,
          messages(application)
        ).toString
      }
    }
  }

}
