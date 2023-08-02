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

package it

import bars.BarsConnector
import bars.barsmodel.request.{BarsBankAccount, BarsSubject, BarsValidateRequest, BarsVerifyPersonalRequest}
import bars.barsmodel.response.{BarsAssessmentType, BarsPreVerifyResponse, BarsVerifyResponse}
import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, post, urlMatching}
import connectors.WireMockHelper
import controllers.actions._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import repositories.SessionRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class BarsConnectorSpec extends SpecBase with WireMockHelper {

  private lazy val app: Application = {
    val dataRequiredAction = mock[DataRequiredAction]
    val sessionRepository  = mock[SessionRepository]

    GuiceApplicationBuilder()
      .configure(
        "microservice.services.bars.port" -> server.port()
      )
      .overrides(
        bind[SessionRepository].toInstance(sessionRepository),
        bind[DataRequiredAction].toInstance(dataRequiredAction),
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(None))
      )
      .build()
  }

  private lazy val connector = app.injector.instanceOf[BarsConnector]

  "bars connector should return a BarsVerifyResponse when a well formed response is received from /verify/personal" in {
    val personalRequest: BarsVerifyPersonalRequest = givenAWellFormedPersonalBarsVerifyRequest
    val responseJson: String                       = Json.toJson(aBarsVerifyResponse).toString()

    givenARequestIsMadeToBarsTheResponseWillBe(personalRequest, responseJson)

    val barsVerifyResponse: BarsVerifyResponse = whenAPersonalRequestIsSentToTheBarsService(personalRequest)

    barsVerifyResponse.accountNumberIsWellFormatted shouldBe BarsAssessmentType.Yes
  }

  "bars connector should return a HttpResponse when a well formed response is received from /validate/bank-details" in {
    val barsValidateRequest: BarsValidateRequest = givenAWellFormedBarsValidateRequest
    val responseJson: String                     = Json.toJson(aBarsValidateResponse).toString()

    givenARequestIsMadeToValidateTheResponseWillBe(barsValidateRequest, responseJson)

    val httpResponse: HttpResponse = whenAValidateRequestIsSentToTheBarsService(barsValidateRequest)

    httpResponse.status shouldBe OK
    httpResponse.body   shouldBe responseJson
  }

  private def whenAPersonalRequestIsSentToTheBarsService(personalRequest: BarsVerifyPersonalRequest) = {
    val hc: HeaderCarrier                               = HeaderCarrier()
    val barsServiceResponse: Future[BarsVerifyResponse] = connector.verifyPersonal(personalRequest)(hc)
    val barsVerifyResponse: BarsVerifyResponse          = barsServiceResponse.futureValue
    barsVerifyResponse
  }

  private def givenAWellFormedPersonalBarsVerifyRequest = {
    val barsBankAccount: BarsBankAccount           = BarsBankAccount("sortCode", "accountNumber")
    val barsSubject: BarsSubject                   = BarsSubject(Some("Mrs"), Some("Firstname Lastname"), None, None, None, None)
    val personalRequest: BarsVerifyPersonalRequest = new BarsVerifyPersonalRequest(barsBankAccount, barsSubject)
    personalRequest
  }

  private def givenARequestIsMadeToBarsTheResponseWillBe(
    personalRequest: BarsVerifyPersonalRequest,
    responseJson: String
  ) =
    server.stubFor(
      post(urlMatching("/verify/personal"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(personalRequest))))
        .willReturn(aResponse().withBody(responseJson).withStatus(OK))
    )

  private def aBarsVerifyResponse =
    BarsVerifyResponse(
      accountNumberIsWellFormatted = BarsAssessmentType.Yes,
      accountExists = BarsAssessmentType.Yes,
      nameMatches = BarsAssessmentType.Yes,
      nonStandardAccountDetailsRequiredForBacs = BarsAssessmentType.Yes,
      sortCodeIsPresentOnEISCD = BarsAssessmentType.Yes,
      sortCodeSupportsDirectDebit = BarsAssessmentType.Yes,
      sortCodeSupportsDirectCredit = BarsAssessmentType.Yes,
      accountName = None,
      sortCodeBankName = None,
      iban = None
    )

  private def aBarsValidateResponse =
    BarsPreVerifyResponse(
      accountNumberIsWellFormatted = BarsAssessmentType.Yes,
      nonStandardAccountDetailsRequiredForBacs = BarsAssessmentType.Yes,
      sortCodeIsPresentOnEISCD = BarsAssessmentType.Yes,
      sortCodeSupportsDirectDebit = Some(BarsAssessmentType.Yes)
    )

  private def whenAValidateRequestIsSentToTheBarsService(barsValidateRequest: BarsValidateRequest) = {
    val hc: HeaderCarrier                         = HeaderCarrier()
    val barsServiceResponse: Future[HttpResponse] = connector.validateBankDetails(barsValidateRequest)(hc)
    val httpResponse: HttpResponse                = barsServiceResponse.futureValue
    httpResponse
  }

  private def givenAWellFormedBarsValidateRequest = {
    val barsBankAccount: BarsBankAccount         = BarsBankAccount("sortCode", "accountNumber")
    val barsValidateRequest: BarsValidateRequest = new BarsValidateRequest(barsBankAccount)
    barsValidateRequest
  }

  private def givenARequestIsMadeToValidateTheResponseWillBe(
    barsValidateRequest: BarsValidateRequest,
    response: String
  ) =
    server.stubFor(
      post(urlMatching("/validate/bank-details"))
        .withRequestBody(equalToJson(Json.stringify(Json.toJson(barsValidateRequest))))
        .willReturn(aResponse().withBody(response).withStatus(OK))
    )
}
