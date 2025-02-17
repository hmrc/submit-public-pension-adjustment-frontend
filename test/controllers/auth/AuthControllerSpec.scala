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

package controllers.auth

import base.SpecBase
import config.FrontendAppConfig
import connectors.SubmitBackendConnector
import models.{Done, InternationalAddress}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import pages.{ClaimOnBehalfPage, PensionSchemeMemberInternationalAddressPage, PensionSchemeMemberResidencePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{SubmissionDataService, UserDataService}

import java.net.URLEncoder
import scala.concurrent.Future

class AuthControllerSpec extends SpecBase with MockitoSugar {

  "signOut" - {

    val userAnswers = emptyUserAnswers
      .set(ClaimOnBehalfPage, true)
      .get
      .set(PensionSchemeMemberResidencePage, false)
      .get
      .set(
        PensionSchemeMemberInternationalAddressPage,
        InternationalAddress(
          None,
          "l1",
          None,
          None,
          "town",
          None,
          None,
          "Antarctica"
        )
      )
      .get

    "must clear user answers and redirect to sign out" in {

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission))
          .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.signOut().url)

        val result = route(application, request).value

        val encodedContinueUrl  =
          URLEncoder.encode("http://localhost:12805/submit-public-pension-adjustment/account/signed-out", "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
      }
    }
  }

  "signOutUnauthorised" - {

    "must redirect to start page" in {

      val application = applicationBuilder(None).build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.signOutUnauthorised().url)

        val result = route(application, request).value

        val encodedContinueUrl  = URLEncoder.encode(appConfig.redirectToStartPage, "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
      }
    }
  }

  "signOutNoSurvey" - {

    "must clear users answers and redirect to sign out, specifying SignedOut as the continue URL" in {

      val mockUserDataService        = mock[UserDataService]
      val mockSubmissionDataService  = mock[SubmissionDataService]
      val mockSubmitBackendConnector = mock[SubmitBackendConnector]

      when(mockUserDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmissionDataService.clear()(any())) thenReturn Future.successful(Done)
      when(mockSubmitBackendConnector.clearCalcUserAnswersSubmitBE()(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(None)
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[SubmissionDataService].toInstance(mockSubmissionDataService),
            bind[SubmitBackendConnector].toInstance(mockSubmitBackendConnector)
          )
          .build()

      running(application) {

        val appConfig = application.injector.instanceOf[FrontendAppConfig]
        val request   = FakeRequest(GET, routes.AuthController.signOutNoSurvey().url)

        val result = route(application, request).value

        val encodedContinueUrl  = URLEncoder.encode(routes.SignedOutController.onPageLoad.url, "UTF-8")
        val expectedRedirectUrl = s"${appConfig.signOutUrl}?continue=$encodedContinueUrl"

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual expectedRedirectUrl
        verify(mockUserDataService, times(1)).clear()(any())
        verify(mockSubmissionDataService, times(1)).clear()(any())
        verify(mockSubmitBackendConnector, times(1)).clearCalcUserAnswersSubmitBE()(any())
      }
    }
  }
}
