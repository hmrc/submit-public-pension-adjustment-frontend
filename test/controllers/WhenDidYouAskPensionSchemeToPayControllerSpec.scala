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

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import forms.WhenDidYouAskPensionSchemeToPayFormProvider
import models.{NormalMode, Period, UserAnswers}
import models.calculation.inputs.CalculationInputs
import models.calculation.response.{CalculationResponse, TotalAmounts}
import models.submission.Submission
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhenDidYouAskPensionSchemeToPayPage
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.{SessionRepository, SubmissionRepository}
import views.html.WhenDidYouAskPensionSchemeToPayView

import scala.concurrent.Future

class WhenDidYouAskPensionSchemeToPayControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new WhenDidYouAskPensionSchemeToPayFormProvider(clockAtFixedInstant)

  private def form = formProvider()

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val whenDidYouAskPensionSchemeToPayRoute =
    routes.WhenDidYouAskPensionSchemeToPayController.onPageLoad(NormalMode, Period._2020).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, whenDidYouAskPensionSchemeToPayRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, whenDidYouAskPensionSchemeToPayRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "WhenDidYouAskPensionSchemeToPay Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val result = route(application, getRequest).value

        val view = application.injector.instanceOf[WhenDidYouAskPensionSchemeToPayView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode, Period._2020)(
          getRequest,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(WhenDidYouAskPensionSchemeToPayPage(Period._2020), validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val view = application.injector.instanceOf[WhenDidYouAskPensionSchemeToPayView]

        val result = route(application, getRequest).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode, Period._2020)(
          getRequest,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSubmissionRepository = mock[SubmissionRepository]
      val mockCalculationInputs    = mock[CalculationInputs]
      val mockSessionRepository    = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val calculationResponse    = CalculationResponse(
        models.calculation.response.Resubmission(false, None),
        TotalAmounts(0, 1, 0),
        List.empty,
        List.empty
      )
      val submission: Submission =
        Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(
            bind[SessionRepository].toInstance(mockSessionRepository),
            bind[SubmissionRepository].toInstance(mockSubmissionRepository)
          )
          .build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      val request =
        FakeRequest(POST, whenDidYouAskPensionSchemeToPayRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[WhenDidYouAskPensionSchemeToPayView]

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
        val result = route(application, getRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val result = route(application, getRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
