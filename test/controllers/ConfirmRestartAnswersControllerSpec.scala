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
import connectors.SubmitBackendConnector
import forms.ConfirmRestartAnswersFormProvider
import models.Done
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}
import views.html.ConfirmRestartAnswersView

import scala.concurrent.Future

class ConfirmRestartAnswersControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new ConfirmRestartAnswersFormProvider()
  val form         = formProvider()

  lazy val confirmRestartAnswersRoute = routes.ConfirmRestartAnswersController.onPageLoad().url

  "ConfirmRestartAnswers Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, confirmRestartAnswersRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[ConfirmRestartAnswersView]

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(form)(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, confirmRestartAnswersRoute)
            .withFormUrlEncodedBody(("value", "false"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` controllers.routes.ContinueChoiceController.onPageLoad().url
      }
    }

    "must clear repositories on submit" in {

      val mockUserDataService = mock[UserDataService]
      val mockSubmissionDataService = mock[SubmissionDataService]
      val mockCalculateBackendDataService = mock[CalculateBackendDataService]
      val mockSubmitBackendConnector = mock[SubmitBackendConnector]

      when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
      when(mockUserDataService.clear()(any())) `thenReturn` Future.successful(Done)
      when(mockSubmissionDataService.clear()(any())) `thenReturn` Future.successful(Done)
      when(mockCalculateBackendDataService.clearSubmissionCalcBE()(any())) `thenReturn` Future.successful(Done)
      when(mockSubmitBackendConnector.clearCalcUserAnswersSubmitBE()(any())) `thenReturn` Future.successful(Done)
      when(mockCalculateBackendDataService.clearUserAnswersCalcBE()(any())) `thenReturn` Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService),
            bind[CalculateBackendDataService].toInstance(mockCalculateBackendDataService),
            bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, confirmRestartAnswersRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        verify(mockUserDataService, times(1)).clear()(any())
        verify(mockSubmissionDataService, times(1)).clear()(any())
        verify(mockSubmitBackendConnector, times(1)).clearCalcUserAnswersSubmitBE()(any())
        verify(mockCalculateBackendDataService, times(1)).clearSubmissionCalcBE()(any())
        verify(mockCalculateBackendDataService, times(1)).clearUserAnswersCalcBE()(any())

      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, confirmRestartAnswersRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = form.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[ConfirmRestartAnswersView]

        val result = route(application, request).value

        status(result) `mustEqual` BAD_REQUEST
        contentAsString(result) `mustEqual` view(boundForm)(request, messages(application)).toString
      }
    }
  }
}
