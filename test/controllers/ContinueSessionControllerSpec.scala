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
import forms.ContinueChoiceFormProvider
import models.{ContinueChoice, Done, NavigationState, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ContinueChoicePage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.ContinueChoiceView

import scala.concurrent.Future

class ContinueSessionControllerSpec extends SpecBase with MockitoSugar {

  "ContinueSession Controller" - {

    "must return to page where user left off in Submission journey" in {

      val answers        = emptyUserAnswers
      val answersWithNav = NavigationState.save(answers, "/submission-service/uk-resident")
      val application    = applicationBuilder(userAnswers = Some(answersWithNav), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ContinueSessionController.continueSession.url)

        val result = route(application, request).value

        redirectLocation(result).value mustEqual "/submission-service/uk-resident"
      }
    }

    "must return to Claim on behalf page where user left off in Submission journey" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.ContinueSessionController.continueSession.url)

        val result = route(application, request).value

        redirectLocation(
          result
        ).value mustEqual "/submit-public-pension-adjustment/submission-service/submitting-on-behalf-someone-else"
      }
    }
  }
}
