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
import models.calculation.response.TaxYearScheme
import models.requests.{AddressLookupAddress, AddressLookupConfirmation, AddressLookupCountry}
import models.submission.Submission
import models.{Done, InternationalAddress, NormalMode, PensionSchemeDetails, Period, StatusOfUser, UkAddress, UserAnswers, WhenWillYouAskPensionSchemeToPay, WhoWillPay}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.mockito.ArgumentCaptor
import org.scalatestplus.mockito.MockitoSugar
import pages._
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService

import java.time.LocalDate
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

          status(result) `mustEqual` SEE_OTHER

          checkNavigation(redirectLocation(result).value, "/there-is-a-problem")
        }
      }

      "should store user answers retrieved from ALF to appropriate to appropriate location" - {

        "when uk address retrieved" - {

          "store uk address to PensionSchemeMemberUKAddressPage and cleanup other answers" in {

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

            val mockAddressLookupConnector = mock[AddressLookupConnector]
            val mockUserDataService        = mock[UserDataService]
            val userAnswersCaptor          = ArgumentCaptor.forClass(classOf[UserAnswers])

            when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
            when(mockAddressLookupConnector.retrieveAddress(any())(any())) `thenReturn` Future.successful(
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
                userAnswers = Some(userAnswers),
                submission = Some(submission)
              )
                .overrides(
                  bind[UserDataService].toInstance(mockUserDataService),
                  bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
                )
                .build()

            running(application) {

              val request = FakeRequest(GET, claimOnBehalfRouteNormalModeWithId)

              val result = route(application, request).value

              status(result) `mustEqual` SEE_OTHER
              verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
              val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
              capturedUserAnswers.get(PensionSchemeMemberUKAddressPage) `mustBe` Some(
                UkAddress(
                  None,
                  "UK Address Line 1",
                  Some("UK Address Line 1"),
                  Some("UK Address Line 3"),
                  "town",
                  None,
                  Some("ZZ1 1ZZ"),
                  Some("United Kingdom")
                )
              )
              capturedUserAnswers.get(PensionSchemeMemberResidencePage) `mustBe` None
              capturedUserAnswers.get(PensionSchemeMemberInternationalAddressPage) `mustBe` None
            }
          }

        }

        "when international address retrieved" - {

          "store uk address to PensionSchemeMemberInternationalAddressPage and cleanup other answers" in {

            val userAnswers = emptyUserAnswers
              .set(ClaimOnBehalfPage, true)
              .get
              .set(PensionSchemeMemberResidencePage, true)
              .get
              .set(
                PensionSchemeMemberUKAddressPage,
                UkAddress(
                  None,
                  "UK Address Line 1",
                  Some("UK Address Line 1"),
                  Some("UK Address Line 3"),
                  "town",
                  None,
                  Some("ZZ1 1ZZ"),
                  Some("United Kingdom")
                )
              )
              .get

            val mockAddressLookupConnector = mock[AddressLookupConnector]
            val mockUserDataService        = mock[UserDataService]
            val userAnswersCaptor          = ArgumentCaptor.forClass(classOf[UserAnswers])

            when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
            when(mockAddressLookupConnector.retrieveAddress(any())(any())) `thenReturn` Future.successful(
              AddressLookupConfirmation(
                "someAuditRef",
                Some("someId"),
                AddressLookupAddress(
                  None,
                  List("Address Line 1", "Address Line 1", "Address Line 3", "town"),
                  None,
                  AddressLookupCountry(
                    "AQ",
                    "Antarctica"
                  )
                )
              )
            )

            val application =
              applicationBuilder(
                userAnswers = Some(userAnswers),
                submission = Some(submission)
              )
                .overrides(
                  bind[UserDataService].toInstance(mockUserDataService),
                  bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
                )
                .build()

            running(application) {

              val request = FakeRequest(GET, claimOnBehalfRouteNormalModeWithId)

              val result = route(application, request).value

              status(result) `mustEqual` SEE_OTHER
              verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
              val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
              capturedUserAnswers.get(PensionSchemeMemberInternationalAddressPage) `mustBe` Some(
                InternationalAddress(
                  None,
                  "Address Line 1",
                  Some("Address Line 1"),
                  Some("Address Line 3"),
                  "town",
                  None,
                  None,
                  "Antarctica"
                )
              )
              capturedUserAnswers.get(PensionSchemeMemberResidencePage) `mustBe` None
              capturedUserAnswers.get(PensionSchemeMemberUKAddressPage) `mustBe` None
            }
          }
        }

        "should cleanup debit loop pages when status of user page == legal representative" in {

          val userAnswers = emptyUserAnswers
            .set(ClaimOnBehalfPage, true)
            .get
            .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
            .get
            .set(WhoWillPayPage(Period._2020), WhoWillPay.PensionScheme)
            .get
            .set(WhichPensionSchemeWillPayPage(Period._2020), "scheme")
            .get
            .set(
              PensionSchemeDetailsPage(Period._2020),
              PensionSchemeDetails(
                "schemeName",
                "pstr"
              )
            )
            .get
            .set(AskedPensionSchemeToPayTaxChargePage(Period._2020), true)
            .get
            .set(WhenWillYouAskPensionSchemeToPayPage(Period._2020), WhenWillYouAskPensionSchemeToPay.JanToMar24)
            .get
            .set(WhenDidYouAskPensionSchemeToPayPage(Period._2020), LocalDate.of(2019, 7, 31))
            .get
            .set(SchemeElectionConsentPage(Period._2020), true)
            .get
            .set(WhoWillPayPage(Period._2021), WhoWillPay.PensionScheme)
            .get

          val mockAddressLookupConnector = mock[AddressLookupConnector]
          val mockUserDataService        = mock[UserDataService]
          val userAnswersCaptor          = ArgumentCaptor.forClass(classOf[UserAnswers])

          when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
          when(mockAddressLookupConnector.retrieveAddress(any())(any())) `thenReturn` Future.successful(
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
              userAnswers = Some(userAnswers),
              submission = Some(submission)
            )
              .overrides(
                bind[UserDataService].toInstance(mockUserDataService),
                bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
              )
              .build()

          running(application) {

            val request = FakeRequest(GET, claimOnBehalfRouteNormalModeWithId)

            val result = route(application, request).value

            status(result) `mustEqual` SEE_OTHER
            verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
            val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
            capturedUserAnswers.get(WhoWillPayPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(WhichPensionSchemeWillPayPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(PensionSchemeDetailsPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(AskedPensionSchemeToPayTaxChargePage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(WhenWillYouAskPensionSchemeToPayPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(WhenDidYouAskPensionSchemeToPayPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(SchemeElectionConsentPage(Period._2020)) `mustBe` None
            capturedUserAnswers.get(WhoWillPayPage(Period._2021)) `mustBe` None
          }
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

          status(result) `mustEqual` SEE_OTHER

          checkNavigation(redirectLocation(result).value, "/there-is-a-problem")
        }
      }

      "should store user answers retrieved from ALF to appropriate to appropriate location" - {

        "when uk address retrieved" - {

          "store uk address to UKAddressPage and cleanup other answers" in {

            val userAnswers = emptyUserAnswers
              .set(ClaimOnBehalfPage, true)
              .get
              .set(AreYouAUKResidentPage, false)
              .get
              .set(
                InternationalAddressPage,
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

            val mockAddressLookupConnector = mock[AddressLookupConnector]
            val mockUserDataService        = mock[UserDataService]
            val userAnswersCaptor          = ArgumentCaptor.forClass(classOf[UserAnswers])

            when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
            when(mockAddressLookupConnector.retrieveAddress(any())(any())) `thenReturn` Future.successful(
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

            val submission: Submission =
              submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))

            val application =
              applicationBuilder(
                userAnswers = Some(userAnswers),
                submission = Some(submission)
              )
                .overrides(
                  bind[UserDataService].toInstance(mockUserDataService),
                  bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
                )
                .build()

            running(application) {

              val request = FakeRequest(GET, userAddressRouteNormalModeWithId)

              val result = route(application, request).value

              status(result) `mustEqual` SEE_OTHER
              verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
              val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
              capturedUserAnswers.get(UkAddressPage) `mustBe` Some(
                UkAddress(
                  None,
                  "UK Address Line 1",
                  Some("UK Address Line 1"),
                  Some("UK Address Line 3"),
                  "town",
                  None,
                  Some("ZZ1 1ZZ"),
                  Some("United Kingdom")
                )
              )
              capturedUserAnswers.get(AreYouAUKResidentPage) `mustBe` None
              capturedUserAnswers.get(InternationalAddressPage) `mustBe` None
            }
          }
        }

        "when international address retrieved" - {

          "store uk address to InternationalAddressPage and cleanup other answers" in {

            val userAnswers = emptyUserAnswers
              .set(ClaimOnBehalfPage, true)
              .get
              .set(PensionSchemeMemberResidencePage, true)
              .get
              .set(
                UkAddressPage,
                UkAddress(
                  None,
                  "UK Address Line 1",
                  Some("UK Address Line 1"),
                  Some("UK Address Line 3"),
                  "town",
                  None,
                  Some("ZZ1 1ZZ"),
                  Some("United Kingdom")
                )
              )
              .get

            val mockAddressLookupConnector = mock[AddressLookupConnector]
            val mockUserDataService        = mock[UserDataService]
            val userAnswersCaptor          = ArgumentCaptor.forClass(classOf[UserAnswers])

            when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
            when(mockAddressLookupConnector.retrieveAddress(any())(any())) `thenReturn` Future.successful(
              AddressLookupConfirmation(
                "someAuditRef",
                Some("someId"),
                AddressLookupAddress(
                  None,
                  List("Address Line 1", "Address Line 1", "Address Line 3", "town"),
                  None,
                  AddressLookupCountry(
                    "AQ",
                    "Antarctica"
                  )
                )
              )
            )

            val submission: Submission =
              submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))

            val application =
              applicationBuilder(
                userAnswers = Some(userAnswers),
                submission = Some(submission)
              )
                .overrides(
                  bind[UserDataService].toInstance(mockUserDataService),
                  bind[AddressLookupConnector].toInstance(mockAddressLookupConnector)
                )
                .build()

            running(application) {

              val request = FakeRequest(GET, userAddressRouteNormalModeWithId)

              val result = route(application, request).value

              status(result) `mustEqual` SEE_OTHER
              verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
              val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
              capturedUserAnswers.get(InternationalAddressPage) `mustBe` Some(
                InternationalAddress(
                  None,
                  "Address Line 1",
                  Some("Address Line 1"),
                  Some("Address Line 3"),
                  "town",
                  None,
                  None,
                  "Antarctica"
                )
              )
              capturedUserAnswers.get(AreYouAUKResidentPage) `mustBe` None
              capturedUserAnswers.get(UkAddressPage) `mustBe` None
            }
          }
        }
      }
    }
  }

  def checkNavigation(nextUrl: String, expectedUrl: String) = {
    val urlWithNoContext = nextUrl.replace("/submit-public-pension-adjustment", "")
    urlWithNoContext must be(expectedUrl)
  }
}
