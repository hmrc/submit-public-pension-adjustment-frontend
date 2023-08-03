/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.EnterAlternativeNameFormProvider
import models.{NormalMode, UserAnswers}
import org.scalatestplus.mockito.MockitoSugar
import pages.EnterAlternativeNamePage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.EnterAlternativeNameView

class EnterAlternativeNameControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new EnterAlternativeNameFormProvider()
  val form         = formProvider()

  lazy val enterAlternativeNameRoute = routes.EnterAlternativeNameController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  "EnterAlternativeName Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, enterAlternativeNameRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[EnterAlternativeNameView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(EnterAlternativeNamePage, "answer").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, enterAlternativeNameRoute)

        val view = application.injector.instanceOf[EnterAlternativeNameView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill("answer"), NormalMode)(request, messages(application)).toString
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, enterAlternativeNameRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[EnterAlternativeNameView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, enterAlternativeNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, enterAlternativeNameRoute)
            .withFormUrlEncodedBody(("value", "answer"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, enterAlternativeNameRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
