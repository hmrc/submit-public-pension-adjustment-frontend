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
import forms.WhenWillYouAskPensionSchemeToPayFormProvider
import models.calculation.inputs.CalculationInputs
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import models.{Done, NormalMode, Period, UserAnswers, WhenWillYouAskPensionSchemeToPay}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhenWillYouAskPensionSchemeToPayPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmissionDataService, UserDataService}
import views.html.WhenWillYouAskPensionSchemeToPayView

import scala.concurrent.Future

class WhenWillYouAskPensionSchemeToPayControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whenWillYouAskPensionSchemeToPayRoute =
    routes.WhenWillYouAskPensionSchemeToPayController.onPageLoad(NormalMode, Period._2020).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val formProvider = new WhenWillYouAskPensionSchemeToPayFormProvider()
  val form         = formProvider()

  "WhenWillYouAskPensionSchemeToPay Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whenWillYouAskPensionSchemeToPayRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhenWillYouAskPensionSchemeToPayView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2020)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhenWillYouAskPensionSchemeToPayPage(Period._2020), WhenWillYouAskPensionSchemeToPay.values.head)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whenWillYouAskPensionSchemeToPayRoute)

        val view = application.injector.instanceOf[WhenWillYouAskPensionSchemeToPayView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(WhenWillYouAskPensionSchemeToPay.values.head),
          NormalMode,
          Period._2020
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService       = mock[UserDataService]
      val mockCalculationInputs     = mock[CalculationInputs]
      val mockSubmissionDataService = mock[SubmissionDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val calculationResponse    = CalculationResponse(
        models.calculation.response.Resubmission(false, None),
        TotalAmounts(0, 1, 0),
        List.empty,
        List.empty
      )
      val submission: Submission =
        Submission("id", "sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whenWillYouAskPensionSchemeToPayRoute)
            .withFormUrlEncodedBody(("value", WhenWillYouAskPensionSchemeToPay.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, whenWillYouAskPensionSchemeToPayRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhenWillYouAskPensionSchemeToPayView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode, Period._2020)(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whenWillYouAskPensionSchemeToPayRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, whenWillYouAskPensionSchemeToPayRoute)
            .withFormUrlEncodedBody(("value", WhenWillYouAskPensionSchemeToPay.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whenWillYouAskPensionSchemeToPayRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
