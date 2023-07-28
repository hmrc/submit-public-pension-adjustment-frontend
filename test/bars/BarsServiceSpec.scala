/*
 * Copyright 2023 HM Revenue & Customs
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

import bars.barsmodel.request.{BarsBankAccount, BarsSubject}
import bars.barsmodel.response.BarsAssessmentType.{Indeterminate, No, Yes}
import bars.barsmodel.response._
import base.SpecBase
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK}
import play.api.libs.json.Json
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, UpstreamErrorResponse}

import scala.concurrent.Future

class BarsServiceSpec extends SpecBase with MockitoSugar {

  val barsConnector         = mock[BarsConnector]
  val headerCarrier         = new HeaderCarrier()
  val bankAccount           = BarsBankAccount.normalise("111111", "11111111")
  val subject               = BarsSubject(None, Some("Testuser One"), None, None, None, None)
  val validValidateResponse = BarsValidateResponse(Yes, Yes, Yes, Some(Yes))

  "BarsService" - {
    "validateBankAccount" - {

      def mockBarsConnectorWithResponse(response: HttpResponse): BarsConnector = {
        when(barsConnector.validateBankDetails(any())(any())).thenReturn(Future.successful(response))
        barsConnector
      }

      "should return BarsErrorResponse when BarsConnector returns a BadRequest response with SORT_CODE_ON_DENY_LIST code" in {
        val barsErrorResponse  = BarsErrorResponse("SORT_CODE_ON_DENY_LIST", "Some error message")
        val badRequestResponse = HttpResponse(BAD_REQUEST, Json.toJson(barsErrorResponse).toString())
        val barsService        = new BarsService(mockBarsConnectorWithResponse(badRequestResponse))

        val response = barsService.validateBankAccount(bankAccount)(headerCarrier)

        response.futureValue shouldBe SortCodeOnDenyList(barsErrorResponse)
      }

      "should throw UpstreamErrorResponse when httpResponse status is not OK or BAD_REQUEST" in {
        val notOKOrBadRequestResponse = HttpResponse(INTERNAL_SERVER_ERROR, "Internal server error occurred")
        val barsService               = new BarsService(mockBarsConnectorWithResponse(notOKOrBadRequestResponse))

        val response = barsService.validateBankAccount(bankAccount)(headerCarrier)

        whenReady(response.failed) { e =>
          e                                                shouldBe an[UpstreamErrorResponse]
          e.asInstanceOf[UpstreamErrorResponse].statusCode shouldBe INTERNAL_SERVER_ERROR
        }
      }

      "should throw IllegalArgumentException when httpResponse.status is OK but httpResponse body cannot be parsed to BarsValidateResponse" in {
        val notParsableOkResponse = HttpResponse(OK, "Non parsable JSON")
        val barsService           = new BarsService(mockBarsConnectorWithResponse(notParsableOkResponse))

        val response = barsService.validateBankAccount(bankAccount)(headerCarrier)

        whenReady(response.failed) { e =>
          e          shouldBe an[IllegalArgumentException]
          e.getMessage should include("Unsupported statusCode 200")
        }
      }

      "should throw UpstreamErrorResponse when httpResponse.status is BAD_REQUEST but httpResponse body cannot be parsed to BarsErrorResponse" in {
        val notParsableBadRequestResponse = HttpResponse(BAD_REQUEST, """{ "malformed": "BarsErrorResponse" }""")
        val barsService                   = new BarsService(mockBarsConnectorWithResponse(notParsableBadRequestResponse))

        val response = barsService.validateBankAccount(bankAccount)(headerCarrier)

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
        when(barsConnector.verifyPersonal(any())(any())).thenReturn(Future.successful(validResponse))

        val response = barsService.verifyPersonal(bankAccount, subject)(headerCarrier)

        response.futureValue shouldBe VerifyResponse(validResponse)
      }

      "should return VerifyResponse when BarsConnector returns a valid response and first and last name are separate" in {
        val barsService = new BarsService(barsConnector)

        val separateSubject = BarsSubject(None, None, Some("Test"), Some("Userone"), None, None)

        val validResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)
        when(barsConnector.verifyPersonal(any())(any())).thenReturn(Future.successful(validResponse))

        val response = barsService.verifyPersonal(bankAccount, separateSubject)(headerCarrier)

        response.futureValue shouldBe VerifyResponse(validResponse)
      }
    }

    "verifyBankDetails" - {
      "should return Left(accountNumberIsWellFormattedNo) when validateBankAccount returns a BarsValidateResponse with no for account well formatted" in {
        val barsService =
          new BarsService(mockConnectorWithValidateResponse(BarsValidateResponse(No, Yes, Yes, Some(No))))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(AccountNumberNotWellFormattedValidateResponse(_)) => }
      }

      "should return Left(sortCodeIsPresentOnEiscdNo) when validateBankAccount returns a BarsValidateResponse with no for sortCodeIsPresentOnEISCD" in {
        val barsService =
          new BarsService(mockConnectorWithValidateResponse(BarsValidateResponse(Yes, Yes, No, Some(No))))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(SortCodeNotPresentOnEiscdValidateResponse(_)) => }
      }

      "should return Left(sortCodeSupportsDirectDebitNo) when validateBankAccount returns a BarsValidateResponse with no for sortCodeSupportsDirectDebit" in {
        val barsService =
          new BarsService(mockConnectorWithValidateResponse(BarsValidateResponse(Yes, Yes, Yes, Some(No))))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(SortCodeDoesNotSupportDirectDebitValidateResponse(_)) => }
      }

      "should return Right(VerifyResponse) when validateBankAccount and verifyPersonal return valid response" in {
        val verifyResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Right(VerifyResponse(`verifyResponse`)) => }
      }

      "should return Left(ThirdPartyError(response)) when verified response is thirdPartyError" in {
        val verifyResponse =
          BarsVerifyResponse(Yes, BarsAssessmentType.Error, Yes, Yes, Yes, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(ThirdPartyError(_)) => }
      }

      "should return Left(AccountNumberNotWellFormatted(response)) when verified response is accountNumberIsWellFormattedNo" in {
        val verifyResponse = BarsVerifyResponse(No, Yes, Yes, Yes, Yes, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(AccountNumberNotWellFormatted(_)) => }
      }

      "should return Left(SortCodeNotPresentOnEiscd(response)) when verified response is sortCodeIsPresentOnEiscdNo" in {
        val verifyResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, No, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(SortCodeNotPresentOnEiscd(_)) => }
      }

      "should return Left(SortCodeDoesNotSupportDirectDebit(response)) when verified response is sortCodeSupportsDirectDebitNo" in {
        val verifyResponse = BarsVerifyResponse(Yes, Yes, Yes, Yes, Yes, No, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(SortCodeDoesNotSupportDirectDebit(_)) => }
      }

      "should return Left(NameDoesNotMatch(response)) when verified response is nameMatchesNo" in {
        val verifyResponse = BarsVerifyResponse(Yes, Yes, No, Yes, Yes, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(NameDoesNotMatch(_)) => }
      }

      "should return Left(AccountDoesNotExist(response)) when verified response is accountDoesNotExist" in {
        val verifyResponse = BarsVerifyResponse(Yes, No, Yes, Yes, Yes, Yes, Yes, None, None, None)

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(AccountDoesNotExist(_)) => }
      }

      "should return Left(OtherBarsError) when verifyPersonal returns an unexpected response" in {
        val verifyResponse = BarsVerifyResponse(
          Indeterminate,
          Indeterminate,
          Indeterminate,
          Indeterminate,
          Indeterminate,
          Indeterminate,
          Indeterminate,
          None,
          None,
          None
        )

        val barsService =
          new BarsService(mockConnectorWithVerifyAndValidateResponse(validValidateResponse, verifyResponse))

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(OtherBarsError(VerifyResponse(`verifyResponse`))) => }
      }

      "should return Left(SortCodeOnDenyListErrorResponse) when validateBankAccount returns SortCodeOnDenyList" in {
        val barsService       = new BarsService(barsConnector)
        val barsErrorResponse = BarsErrorResponse("SORT_CODE_ON_DENY_LIST", "Some error message")

        when(barsConnector.validateBankDetails(any())(any()))
          .thenReturn(
            Future.successful(
              HttpResponse(BAD_REQUEST, Json.toJson(barsErrorResponse).toString())
            )
          )

        val response = barsService.verifyBankDetails(bankAccount, subject)(headerCarrier)
        response.futureValue should matchPattern { case Left(SortCodeOnDenyListErrorResponse(_)) => }
      }
    }
  }

  def mockConnectorWithValidateResponse(validateResponse: BarsValidateResponse): BarsConnector = {
    when(barsConnector.validateBankDetails(any())(any()))
      .thenReturn(
        Future.successful(HttpResponse(OK, Json.toJson(validateResponse).toString))
      )
    barsConnector
  }

  def mockConnectorWithVerifyAndValidateResponse(
    validateResponse: BarsValidateResponse,
    verifyResponse: BarsVerifyResponse
  ): BarsConnector = {
    when(barsConnector.validateBankDetails(any())(any()))
      .thenReturn(
        Future.successful(HttpResponse(OK, Json.toJson(validateResponse).toString))
      )
    when(barsConnector.verifyPersonal(any())(any())).thenReturn(Future.successful(verifyResponse))
    barsConnector
  }
}
