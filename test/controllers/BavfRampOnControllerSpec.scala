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
import connectors.BavfConnector
import models.{CheckMode, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class BavfRampOnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val routeNormalMode = routes.BavfRampOnController.rampOnBavf(NormalMode).url
  lazy val routeCheckMode  = routes.BavfRampOnController.rampOnBavf(CheckMode).url
  lazy val handlerRoute    =
    routes.BavfRampOnController.existingUrlFragmentBavfHandler(NormalMode).url

  "BavfRampOnController" - {

    "must redirect to Bavf in normal mode" in {

      val mockBavfConnector = mock[BavfConnector]

      when(mockBavfConnector.initialiseJourney(any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers),
          submission = Some(submission)
        )
          .overrides(bind[BavfConnector].toInstance(mockBavfConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, routeNormalMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to Bavf in check mode" in {

      val mockBavfConnector = mock[BavfConnector]

      when(mockBavfConnector.initialiseJourney(any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers),
          submission = Some(submission)
        )
          .overrides(bind[BavfConnector].toInstance(mockBavfConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, routeCheckMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to Bavf when user accesses deprecated URL fragments " in {

      val mockBavfConnector = mock[BavfConnector]

      when(mockBavfConnector.initialiseJourney(any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers),
          submission = Some(submission)
        )
          .overrides(bind[BavfConnector].toInstance(mockBavfConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, handlerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }
  }
}
