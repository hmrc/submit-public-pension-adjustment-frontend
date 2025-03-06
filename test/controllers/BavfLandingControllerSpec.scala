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
import models.bavf.{BavfCompleteResponse, BavfPersonalCompleteResponse, ReputationResponseEnum}
import models.{BankDetails, Done, NormalMode, UserAnswers}
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

import scala.concurrent.Future

class BavfLandingControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val routeNormalMode =
    routes.BavfLandingController.redirectBavf("bavfid", NormalMode).url

  "BavfLandingController" - {

    "redirectBavf" - {

      "should store user answers retrieved from BAVF to bankdetails" in {

        val userAnswers = emptyUserAnswers

        val mockBavfConnector   = mock[BavfConnector]
        val mockUserDataService = mock[UserDataService]
        val userAnswersCaptor   = ArgumentCaptor.forClass(classOf[UserAnswers])

        when(mockUserDataService.set(any())(any())) `thenReturn` Future.successful(Done)
        when(mockBavfConnector.retrieveBarsDetails(any())(any(), any())) `thenReturn` Future.successful(
          BavfCompleteResponse(
            "personal",
            Some(
              BavfPersonalCompleteResponse(
                None,
                "Test Man",
                "sortcode",
                "accountNumber",
                ReputationResponseEnum.Yes,
                None,
                Some(ReputationResponseEnum.Yes),
                Some(ReputationResponseEnum.Yes),
                None,
                Some(ReputationResponseEnum.No),
                Some("BARCLAYS BANK UK PLC"),
                Some(ReputationResponseEnum.Yes),
                Some(ReputationResponseEnum.Yes),
                Some("GB21BARC20710644344677")
              )
            ),
            None
          )
        )

        val application =
          applicationBuilder(
            userAnswers = Some(userAnswers),
            submission = Some(submission)
          )
            .overrides(
              bind[UserDataService].toInstance(mockUserDataService),
              bind[BavfConnector].toInstance(mockBavfConnector)
            )
            .build()

        running(application) {

          val request = FakeRequest(GET, routeNormalMode)

          val result = route(application, request).value

          status(result) `mustEqual` SEE_OTHER
          verify(mockUserDataService).set(userAnswersCaptor.capture())(any())
          val capturedUserAnswers: UserAnswers = userAnswersCaptor.getValue
          capturedUserAnswers.get(BankDetailsPage) `mustBe` Some(
            BankDetails("Test Man", "sortcode", "accountNumber", None)
          )
        }
      }
    }
  }
}
