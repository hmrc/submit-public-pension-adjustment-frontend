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
import config.FrontendAppConfig
import connectors.SubmitBackendConnector
import models.{Done, UserAnswers, UserSubmissionReference}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{CalculateBackendDataService, SubmissionDataService, SubmissionService, UserDataService}
import views.html.SubmissionView

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase with MockitoSugar {

  private val mockSubmissionService = mock[SubmissionService]

  lazy val submissionRoute = routes.SubmissionController.onPageLoad().url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  "Submission Controller" - {

    "must return OK and the correct view for a GET" in {

      val mockUserDataService             = mock[UserDataService]
      val mockSubmissionDataService       = mock[SubmissionDataService]
      val mockCalculateBackendDataService = mock[CalculateBackendDataService]
      val mockSubmitBackendConnector      = mock[SubmitBackendConnector]

      when(mockUserDataService.clear()(any())) `thenReturn` Future.successful(Done)
      when(mockSubmissionDataService.clear()(any())) `thenReturn` Future.successful(Done)
      when(mockCalculateBackendDataService.clearUserAnswersCalcBE()(any())) `thenReturn` Future.successful(Done)
      when(mockCalculateBackendDataService.clearSubmissionCalcBE()(any())) `thenReturn` Future.successful(Done)
      when(mockSubmitBackendConnector.clearCalcUserAnswersSubmitBE()(any())) `thenReturn` Future.successful(Done)

      val userAnswers =
        UserAnswers(userAnswersId).set(UserSubmissionReference(), "userSubmissionReference").success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission))
        .overrides(
          bind[SubmissionService].toInstance(mockSubmissionService),
          bind[UserDataService].toInstance(mockUserDataService),
          bind[SubmissionDataService].toInstance(mockSubmissionDataService),
          bind[CalculateBackendDataService].toInstance(mockCalculateBackendDataService),
          bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector)
        )
        .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]

        val request = FakeRequest(GET, submissionRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SubmissionView]

        val expectedRedirectUrl = s"${appConfig.exitSurveyUrl}"

        status(result) `mustEqual` OK
        contentAsString(result) `mustEqual` view(
          "userSubmissionReference",
          expectedRedirectUrl
        )(request, messages(application)).toString
        verify(mockUserDataService, times(1)).clear()(any())
        verify(mockSubmissionDataService, times(1)).clear()(any())
        verify(mockCalculateBackendDataService, times(1)).clearUserAnswersCalcBE()(any())
        verify(mockCalculateBackendDataService, times(1)).clearSubmissionCalcBE()(any())
        verify(mockSubmitBackendConnector, times(1)).clearCalcUserAnswersSubmitBE()(any())

      }
    }

    "must redirect to Journey Controller for a GET if no user found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
        .overrides(
          bind[SubmissionService].toInstance(mockSubmissionService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, submissionRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, submissionRoute)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` calculationPrerequisiteRoute
      }
    }
  }
}
