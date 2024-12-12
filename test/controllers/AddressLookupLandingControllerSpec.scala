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
import models.NormalMode
import models.requests.{AddressLookupAddress, AddressLookupConfirmation, AddressLookupCountry}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.ClaimOnBehalfPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService

import scala.concurrent.Future

class AddressLookupLandingControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val claimOnBehalfRouteNormalModeWithId    =
    routes.AddressLookupLandingController.redirectClaimOnBehalf(Some("claimOnBehalf"), NormalMode).url
  lazy val claimOnBehalfRouteNormalModeWithoutId =
    routes.AddressLookupLandingController.redirectClaimOnBehalf(None, NormalMode).url
  lazy val userAddressRouteNormalModeWithId      =
    routes.AddressLookupLandingController.redirectUserAddress(Some("userAddress"), NormalMode).url
  lazy val userAddressRouteNormalModeWithoutId   =
    routes.AddressLookupLandingController.redirectUserAddress(None, NormalMode).url

  "AddressLookupLandingController" - {

    "claim on behalf" - {

      "if no user id found, redirect to journey recovery" in {

        val mockAddressLookupConnector = mock[AddressLookupConnector]

        val application =
          applicationBuilder(
            userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
            submission = Some(submission)
          )
            .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
            .build()

        running(application) {

          val request = FakeRequest(GET, claimOnBehalfRouteNormalModeWithoutId)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          checkNavigation(redirectLocation(result).value, "/there-is-a-problem")
        }
      }

      "should store user answers retrieved from ALF to appropriate to appropriate location" - {

        "when uk address retrieved" - {

          "store uk address to PensionSchemeMemberUKAddressPage and cleanup other answers" in {

            val mockAddressLookupConnector = mock[AddressLookupConnector]
            val mockUserDataService        = mock[UserDataService]

            when(mockAddressLookupConnector.retrieveAddress(any())(any())) thenReturn Future.successful(
              AddressLookupConfirmation(
                "someAuditRef",
                Some("someId"),
                AddressLookupAddress(
                  None,
                  List("UK Address Line 1", "UK Address Line 1", "UK Address Line 3", "town"),
                  Some("ZZ1 1ZZ"),
                  AddressLookupCountry(
                    "GB",
                    "United Kingdom"
                  )
                )
              )
            )

            val application =
              applicationBuilder(
                userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
                submission = Some(submission)
              )
                .overrides(
                  bind[AddressLookupConnector].toInstance(mockAddressLookupConnector),
                  bind[UserDataService].toInstance(mockUserDataService)
                )
                .build()

            running(application) {

              val request = FakeRequest(GET, claimOnBehalfRouteNormalModeWithId)

              val result = route(application, request).value

              // TO FINISH
            }

          }

        }

        "when international address retrieved" - {

          "store uk address to PensionSchemeMemberInternationalAddressPage and cleanup other answers" in {}
        }
      }
    }

    "user address" - {

      "if no user id found, redirect to journey recovery" in {

        val mockAddressLookupConnector = mock[AddressLookupConnector]

        val application =
          applicationBuilder(
            userAnswers = Some(emptyUserAnswers.set(ClaimOnBehalfPage, true).get),
            submission = Some(submission)
          )
            .overrides(bind[AddressLookupConnector].toInstance(mockAddressLookupConnector))
            .build()

        running(application) {

          val request = FakeRequest(GET, userAddressRouteNormalModeWithoutId)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          checkNavigation(redirectLocation(result).value, "/there-is-a-problem")
        }
      }

      "should store user answers retrieved from ALF to appropriate to appropriate location" - {

        "when uk address retrieved" - {

          "store uk address to UKAddressPage and cleanup other answers" in {}

        }

        "when international address retrieved" - {

          "store uk address to InternationalAddressPage and cleanup other answers" in {}
        }
      }
    }
  }

  def checkNavigation(nextUrl: String, expectedUrl: String) = {
    val urlWithNoContext = nextUrl.replace("/submit-public-pension-adjustment", "")
    urlWithNoContext must be(expectedUrl)
  }
}
