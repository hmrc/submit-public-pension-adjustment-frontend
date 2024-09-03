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

package bars

import bars.barsmodel.request.{BarsBankAccount, BarsSubject, BarsVerifyPersonalRequest}
import bars.barsmodel.response.BarsAssessmentType.{No, Yes}
import bars.barsmodel.response._
import base.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BarsServiceSpec extends SpecBase with MockitoSugar {

  val barsConnector = mock[BarsConnector]
  val headerCarrier = new HeaderCarrier()
  val bankAccount   = BarsBankAccount.normalise("111111", "11111111")
  val subject       = BarsSubject(None, Some("Test User"), None, None, None, None)

  "BarsService" - {
    "preVerify" - {

      def mockBarsConnectorWithResponse(response: Future[HttpResponse]): BarsConnector = {
        when(barsConnector.verifyPersonal(any())(any())).thenReturn(response)
        barsConnector
      }

      "should return BarsErrorResponse when BarsConnector returns a BadRequest response with SORT_CODE_ON_DENY_LIST code" in {
        val barsErrorResponse  = BarsErrorResponse("SORT_CODE_ON_DENY_LIST", "Some error message")
        val badRequestResponse = Future.successful(HttpResponse(BAD_REQUEST, Json.toJson(barsErrorResponse).toString()))
        val barsService        = new BarsService(mockBarsConnectorWithResponse(badRequestResponse))

        val response = barsService.preVerify(badRequestResponse)(headerCarrier)

        response.futureValue shouldBe SortCodeOnDenyList(barsErrorResponse)
      }

      "should throw UpstreamErrorResponse when httpResponse status is not OK or BAD_REQUEST" in {
        val notOKOrBadRequestResponse =
          Future.successful(HttpResponse(INTERNAL_SERVER_ERROR, "Internal server error occurred"))
        val barsService               = new BarsService(mockBarsConnectorWithResponse(notOKOrBadRequestResponse))

        val response = barsService.preVerify(notOKOrBadRequestResponse)(headerCarrier)

        whenReady(response.failed) { e =>
          e                                                shouldBe an[UpstreamErrorResponse]
          e.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe INTERNAL_SERVER_ERROR
        }
      }

      "should throw UpstreamErrorResponse when httpResponse.status is OK but httpResponse body cannot be parsed to BarsValidateResponse" in {
        val notParsableOkResponse = Future.successful(HttpResponse(OK, "Non parsable JSON"))
        val barsService           = new BarsService(mockBarsConnectorWithResponse(notParsableOkResponse))

        val response = barsService.preVerify(notParsableOkResponse)(headerCarrier)

        whenReady(response.failed) { e =>
          e shouldBe UpstreamErrorResponse("Non parsable JSON", OK)
        }
      }

      "should throw UpstreamErrorResponse when httpResponse.status is BAD_REQUEST but httpResponse body cannot be parsed to BarsErrorResponse" in {
        val notParsableBadRequestResponse =
          Future.successful(HttpResponse(BAD_REQUEST, """{ "malformed": "BarsErrorResponse" }"""))
        val barsService                   = new BarsService(mockBarsConnectorWithResponse(notParsableBadRequestResponse))

        val response = barsService.preVerify(notParsableBadRequestResponse)(headerCarrier)

        whenReady(response.failed) { e =>
          e                                                shouldBe an[UpstreamErrorResponse]
          e.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe BAD_REQUEST
        }
      }
    }

    "verifyPersonal" - {
      "should return VerifyResponse when BarsConnector returns a valid response" in {
        val barsService = new BarsService(barsConnector)

        val validResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)
        when(barsConnector.verifyPersonal(any())(any()))
          .thenReturn(Future.successful(HttpResponse(OK, Json.toJson(validResponse).toString())))

        val response = barsService.verifyPersonal(
          barsConnector.verifyPersonal(BarsVerifyPersonalRequest(bankAccount, subject))(headerCarrier)
        )(headerCarrier)

        response.futureValue shouldBe VerifyResponse(validResponse)
      }

      "should return VerifyResponse when BarsConnector returns a valid response and first and last name are separate" in {
        val barsService = new BarsService(barsConnector)

        val separateSubject = BarsSubject(None, None, Some("Test"), Some("User"), None, None)

        val validResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)
        when(barsConnector.verifyPersonal(any())(any()))
          .thenReturn(Future.successful(HttpResponse(OK, Json.toJson(validResponse).toString())))

        val response = barsService.verifyPersonal(
          barsConnector.verifyPersonal(BarsVerifyPersonalRequest(bankAccount, separateSubject))(headerCarrier)
        )(headerCarrier)

        response.futureValue shouldBe VerifyResponse(validResponse)
      }
    }

    "verifyBankDetails" - {
      "should return Right[VerifyResponse] when BarsConnector returns a valid response" in {
        val barsService = new BarsService(barsConnector)

        val validVerifyResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)

        val httpResponse = Future.successful(HttpResponse(OK, Json.toJson(validVerifyResponse).toString()))

        when(barsConnector.verifyPersonal(any())(any())).thenReturn(httpResponse)

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)

        response.futureValue shouldBe Right(VerifyResponse(validVerifyResponse))
      }

      "should return Left[BarsError] when preVerify returns SortCodeOnDenyList" in {
        val barsService = new BarsService(barsConnector)

        val barsErrorResponse = BarsErrorResponse("SORT_CODE_ON_DENY_LIST", "Some error message")
        when(barsConnector.verifyPersonal(any())(any()))
          .thenReturn(Future.successful(HttpResponse(BAD_REQUEST, Json.toJson(barsErrorResponse).toString())))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)

        response.futureValue shouldBe Left(SortCodeOnDenyListErrorResponse(SortCodeOnDenyList(barsErrorResponse)))
      }

      "should return Left[ThirdPartyError] when BarsConnector returns a ThirdPartyError response" in {
        val barsService = new BarsService(barsConnector)

        val thirdPartyErrorResponse =
          BarsVerifyResponse(Yes, BarsAssessmentType.Error, Yes, Yes, Yes, Yes, Yes, None, None, None)
        val httpResponse            = Future.successful(HttpResponse(OK, Json.toJson(thirdPartyErrorResponse).toString()))

        when(barsConnector.verifyPersonal(any())(any())).thenReturn(httpResponse)

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)

        response.futureValue shouldBe Left(ThirdPartyError(VerifyResponse(thirdPartyErrorResponse)))
      }

      "should return Left[BarsError] for each defined error in verifyPersonal" in {
        val barsService = new BarsService(barsConnector)

        val errorResponses = List(
          BarsVerifyResponse(No, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None) -> AccountNumberNotWellFormatted,
          BarsVerifyResponse(Yes, Yes, No, Yes, Yes, Yes, Yes, None, None, None) -> NameDoesNotMatch,
          BarsVerifyResponse(Yes, No, Yes, Yes, Yes, Yes, Yes, None, None, None) -> AccountDoesNotExist,
          BarsVerifyResponse(Yes, Yes, Yes, Yes, No, Yes, Yes, None, None, None) -> SortCodeNotPresentOnEiscd,
          BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, No, Yes, None, None, None) -> SortCodeDoesNotSupportDirectDebit,
          BarsVerifyResponse(
            Yes,
            BarsAssessmentType.Error,
            Yes,
            Yes,
            Yes,
            Yes,
            Yes,
            None,
            None,
            None
          )                                                                      -> ThirdPartyError
        )

        for ((barsVerifyResponse, expectedError) <- errorResponses) {
          val httpResponse = Future.successful(HttpResponse(OK, Json.toJson(barsVerifyResponse).toString()))
          when(barsConnector.verifyPersonal(any())(any())).thenReturn(httpResponse)

          val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)

          response.futureValue shouldBe Left(expectedError(VerifyResponse(barsVerifyResponse)))
        }
      }
    }
  }
}
