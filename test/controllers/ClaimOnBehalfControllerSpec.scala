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
import forms.ClaimOnBehalfFormProvider
import models.calculation.inputs.{AnnualAllowance, CalculationInputs, Resubmission}
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{Done, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimOnBehalfPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.SubmissionDataService
import services.UserDataService
import views.html.ClaimOnBehalfView

import scala.concurrent.Future

class ClaimOnBehalfControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ClaimOnBehalfFormProvider()
  val form         = formProvider()

  lazy val claimOnBehalfRoute = routes.ClaimOnBehalfController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  "ClaimOnBehalf Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, claimOnBehalfRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ClaimOnBehalfView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(ClaimOnBehalfPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, claimOnBehalfRoute)

        val view = application.injector.instanceOf[ClaimOnBehalfView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(true), NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService         = mock[UserDataService]
      val mockSubmissionDataService   = mock[SubmissionDataService]
      val mockCalculationInputsWithAA = CalculationInputs(mock[Resubmission], Some(mock[AnnualAllowance]), None)

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val calculationResponse    = CalculationResponse(
        models.calculation.response.Resubmission(false, None),
        TotalAmounts(0, 1, 0),
        List.empty,
        List.empty
      )
      val submission: Submission =
        Submission("sessionId", "submissionUniqueId", mockCalculationInputsWithAA, Some(calculationResponse))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, claimOnBehalfRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val userAnswers = emptyUserAnswers.set(ClaimOnBehalfPage, true)
        val result      = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual ClaimOnBehalfPage
          .navigate(NormalMode, userAnswers.get, submission)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, claimOnBehalfRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ClaimOnBehalfView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, claimOnBehalfRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
