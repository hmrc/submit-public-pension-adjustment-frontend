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
import connectors.{CalculateBackendConnector, SubmitBackendConnector}
import models.Done
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.mockito.MockitoSugar.mock
import org.scalatest.concurrent.Eventually.eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Seconds, Span}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}

import scala.concurrent.Future

class EditCalculationControllerSpec extends SpecBase {

  "EditCalculationController Controller" - {

    "must redirect to calculation task list" in {

      val mockCalculateBackendConnector   = mock[CalculateBackendConnector]
      val mockUserDataService             = mock[UserDataService]
      val mockSubmissionDataService       = mock[SubmissionDataService]
      val mockSubmitBackendConnector      = mock[SubmitBackendConnector]
      val mockCalculateBackendDataService = mock[CalculateBackendDataService]

      when(mockCalculateBackendConnector.updateCalcBEWithUserAnswers(any())(any())) thenReturn Future.successful(Done)
      when(mockCalculateBackendConnector.sendFlagResetSignal(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmissionDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendConnector.clearCalcUserAnswersSubmitBE()(any())) thenReturn Future.successful(Done)
      when(mockCalculateBackendDataService.clearSubmissionCalcBE()(any())) thenReturn Future.successful(Done)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
        .overrides(
          bind[CalculateBackendConnector].toInstance(mockCalculateBackendConnector),
          bind[UserDataService].toInstance(mockUserDataService),
          bind[SubmissionDataService].toInstance(mockSubmissionDataService),
          bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector),
          bind[CalculateBackendDataService].toInstance(mockCalculateBackendDataService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.EditCalculationController.editCalculation.url)

        val result = route(application, request).value

        eventually(Timeout(Span(30, Seconds))) {
          verify(mockCalculateBackendConnector, times(1)).updateCalcBEWithUserAnswers(any())(any())
          verify(mockCalculateBackendConnector, times(1)).sendFlagResetSignal(any())(any())
          verify(mockUserDataService, times(1)).clear()(any())
          verify(mockSubmissionDataService, times(1)).clear()(any())
          verify(mockSubmitBackendConnector, times(1)).clearCalcUserAnswersSubmitBE()(any())
          verify(mockCalculateBackendDataService, times(1)).clearSubmissionCalcBE()(any())

          redirectLocation(
            result
          ).value must endWith("/public-pension-adjustment/task-list")
        }
      }
    }

    "must handle service failure when a api call fails" in {

      val mockCalculateBackendConnector   = mock[CalculateBackendConnector]
      val mockUserDataService             = mock[UserDataService]
      val mockSubmissionDataService       = mock[SubmissionDataService]
      val mockSubmitBackendConnector      = mock[SubmitBackendConnector]
      val mockCalculateBackendDataService = mock[CalculateBackendDataService]

      when(mockCalculateBackendConnector.updateCalcBEWithUserAnswers(any())(any())) thenReturn Future.successful(Done)
      when(mockCalculateBackendConnector.sendFlagResetSignal(any())(any())) thenReturn Future.successful(Done)
      when(mockUserDataService.clear()(any())) thenReturn Future.failed(new RuntimeException("Service failed"))
      when(mockSubmissionDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendConnector.clearCalcUserAnswersSubmitBE()(any())) thenReturn Future.successful(Done)
      when(mockCalculateBackendDataService.clearSubmissionCalcBE()(any())) thenReturn Future.successful(Done)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
        .overrides(
          bind[CalculateBackendConnector].toInstance(mockCalculateBackendConnector),
          bind[UserDataService].toInstance(mockUserDataService),
          bind[SubmissionDataService].toInstance(mockSubmissionDataService),
          bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector),
          bind[CalculateBackendDataService].toInstance(mockCalculateBackendDataService)
        )
        .build()

      running(application) {
        val request = FakeRequest(GET, routes.EditCalculationController.editCalculation.url)

        intercept[RuntimeException] {
          await(route(application, request).value)
        }.getMessage mustBe "Service failed"
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {
      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = None)
        .overrides()
        .build()
      running(application) {
        val request = FakeRequest(GET, routes.EditCalculationController.editCalculation.url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.CalculationPrerequisiteController.onPageLoad().url
      }
    }
  }
}
