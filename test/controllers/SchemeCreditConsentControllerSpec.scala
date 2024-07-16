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
import models.{Done, SchemeCreditConsent, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar.mock
import play.api.inject
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.SchemeCreditConsentView

import scala.concurrent.Future

class SchemeCreditConsentControllerSpec extends SpecBase {

  "SchemeCreditConsent Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.SchemeCreditConsentController.onPageLoad().url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SchemeCreditConsentView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view()(request, messages(application)).toString
      }
    }

    "Must set SchemeCreditConsent on submit and redirect to next page" in {
      val ua = emptyUserAnswers

      val mockUserDataService = mock[UserDataService]

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])

      when(mockUserDataService.set(userAnswersCaptor.capture())(any())) thenReturn Future.successful(Done)

      val application = applicationBuilder(userAnswers = Some(ua), submission = Some(submission))
        .overrides(
          inject.bind[UserDataService].toInstance(mockUserDataService)
        )
        .build()

      running(application) {
        val request = FakeRequest(POST, routes.SchemeCreditConsentController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        val capturedUserAnswers = userAnswersCaptor.getValue
        capturedUserAnswers.get(SchemeCreditConsent) mustBe Some(true)
        redirectLocation(result).value mustEqual controllers.routes.DeclarationsController.onPageLoad.url
      }
    }
  }
}
