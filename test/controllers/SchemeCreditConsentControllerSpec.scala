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
import forms.SchemeCreditConsentFormProvider
import models.{Done, NormalMode, SchemeCreditConsent, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.SchemeCreditConsentPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SchemeCreditConsentView
import services.UserDataService

import scala.concurrent.Future

class SchemeCreditConsentControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val schemeCreditConsentRoute     = routes.SchemeCreditConsentController.onPageLoad.url
  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val formProvider = new SchemeCreditConsentFormProvider()
  val form         = formProvider()

  "SchemeCreditConsent Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, schemeCreditConsentRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SchemeCreditConsentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual view(form)(request, messages(application)).toString
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, schemeCreditConsentRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers =
        UserAnswers(userAnswersId).set(SchemeCreditConsentPage, SchemeCreditConsent.values.toSet).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, schemeCreditConsentRoute)

        val view = application.injector.instanceOf[SchemeCreditConsentView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(SchemeCreditConsent.values.toSet))(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, schemeCreditConsentRoute)
            .withFormUrlEncodedBody(("value[0]", SchemeCreditConsent.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.DeclarationsController.onPageLoad.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, schemeCreditConsentRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[SchemeCreditConsentView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, schemeCreditConsentRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, schemeCreditConsentRoute)
            .withFormUrlEncodedBody(("value[0]", SchemeCreditConsent.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }
  }
}
