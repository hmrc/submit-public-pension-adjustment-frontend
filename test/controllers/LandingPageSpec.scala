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
import models.UniqueId
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class LandingPageSpec extends SpecBase with MockitoSugar {

  "LandingPage Controller" - {

    lazy val landingPageRoute =
      routes.LandingPageController.onPageLoad(Some(UniqueId("12345678-1234-1234-1234-123412341234"))).url

    "must redirect to first data capture page when submission can be retrieved" in {

      val mockSubmitBackendConnector = mock[SubmitBackendConnector]
      when(mockSubmitBackendConnector.sendSubmissionSignal(any())(any()))
        .thenReturn(Future.successful(true))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, landingPageRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        checkNavigation(redirectLocation(result).get, "/submission-info")
      }
    }

    "must redirect to calculation prerequisite page when submission cannot be retrieved" in {

      val mockSubmitBackendConnector = mock[SubmitBackendConnector]
      when(mockSubmitBackendConnector.sendSubmissionSignal(any())(any()))
        .thenReturn(Future.successful(false))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, landingPageRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        checkNavigation(redirectLocation(result).get, "/calculation-not-complete")
      }
    }

    "must return bad request and error page if uniqueId is invalid format" in {

      lazy val landingPageRoute =
        routes.LandingPageController.onPageLoad(Some(UniqueId("invalidUniqueId"))).url

      val mockSubmitBackendConnector = mock[SubmitBackendConnector]
      when(mockSubmitBackendConnector.sendSubmissionSignal(any())(any()))
        .thenReturn(Future.successful(true))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(
            bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector)
          )
          .build()

      running(application) {
        val request = FakeRequest(GET, landingPageRoute)

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result).contains("Please check that you have entered the correct web address.") must be(true)
      }
    }
  }

  def checkNavigation(nextUrl: String, expectedUrl: String) = {
    val urlWithNoContext = nextUrl.replace("/submit-public-pension-adjustment", "")
    urlWithNoContext must be(expectedUrl)
  }
}
