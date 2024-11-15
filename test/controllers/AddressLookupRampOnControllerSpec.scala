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
import connectors.AddressLookupConnector
import models.{CheckMode, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimOnBehalfPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class AddressLookupRampOnControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val claimOnBhealfHandlerRoute    =
    routes.AddressLookupRampOnController.existingUrlFragmentClaimOnBehalfHandler(NormalMode).url
  lazy val userAddressHandlerRoute      =
    routes.AddressLookupRampOnController.existingUrlFragmentUserAddressHandler(NormalMode).url
  lazy val claimOnBehalfRouteNormalMode = routes.AddressLookupRampOnController.rampOnClaimOnBehalf(NormalMode).url
  lazy val claimOnBehalfRouteCheckMode  = routes.AddressLookupRampOnController.rampOnClaimOnBehalf(CheckMode).url
  lazy val userAddressRouteNormalMode   = routes.AddressLookupRampOnController.rampOnUserAddress(NormalMode).url
  lazy val userAddressRouteCheckMode    = routes.AddressLookupRampOnController.rampOnUserAddress(CheckMode).url

  "AddressLookupRampOnController" - {

    "must redirect to ALF in a claim on behalf scenario in normal mode" in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, claimOnBehalfRouteNormalMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to ALF in a claim on behalf scenario in check mode" in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, claimOnBehalfRouteCheckMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to ALF in a claim on behalf scenario when user accesses deprecated URL fragments " in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, claimOnBhealfHandlerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }

    "must redirect to ALF in user address scenario in normal mode" in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, userAddressRouteNormalMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"

      }
    }

    "must redirect to ALF in user address scenario in check mode" in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, userAddressRouteCheckMode)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"

      }
    }

    "must redirect to ALF in a user address scenario when user accesses deprecated URL fragments " in {

      val mockAddressLookupConnector = mock[AddressLookupConnector]

      when(mockAddressLookupConnector.initialiseJourney(any(), any(), any())(any(), any())) thenReturn Future
        .successful("/redirectUrl")

      val application =
        applicationBuilder(
          userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
          submission = Some(submission)
        )
          .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
          .build()

      running(application) {

        val request = FakeRequest(GET, userAddressHandlerRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          "/redirectUrl"
      }
    }
  }
}
