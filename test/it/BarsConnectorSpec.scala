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

package it

import bars.BarsConnector
import bars.barsmodel.request.{BarsBankAccount, BarsSubject, BarsVerifyPersonalRequest}
import bars.barsmodel.response.{BarsAssessmentType, BarsVerifyResponse}
import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import connectors.WireMockHelper
import controllers.actions._
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.Application
import play.api.http.HeaderNames
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import services.UserDataService
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class BarsConnectorSpec extends SpecBase with WireMockHelper {

  private lazy val app: Application = {
    val dataRequiredAction = mock[DataRequiredAction]
    val userDataService    = mock[UserDataService]

    GuiceApplicationBuilder()
      .configure(
        "microservice.services.bars.port" -> server.port()
      )
      .overrides(
        bind[UserDataService].toInstance(userDataService),
        bind[DataRequiredAction].toInstance(dataRequiredAction),
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[DataRetrievalAction].toInstance(new FakeDataRetrievalAction(None, None))
      )
      .build()
  }

  private lazy val connector = app.injector.instanceOf[BarsConnector]

  "bars connector should return a HttpResponse when a well formed response is received from /verify/personal" in {
    val personalRequest: BarsVerifyPersonalRequest = givenAWellFormedPersonalBarsVerifyRequest
    val responseJson: String                       = Json.toJson(aBarsVerifyResponse).toString()

    givenARequestIsMadeToBarsTheResponseWillBe(personalRequest, responseJson)

    val httpResponse: HttpResponse = whenAPersonalRequestIsSentToTheBarsService(personalRequest)

    httpResponse.status shouldBe OK
    httpResponse.body   shouldBe responseJson
  }

  private def whenAPersonalRequestIsSentToTheBarsService(personalRequest: BarsVerifyPersonalRequest) = {
    val hc: HeaderCarrier                         = HeaderCarrier()
    val barsServiceResponse: Future[HttpResponse] = connector.verifyPersonal(personalRequest)(hc)
    val httpResponse: HttpResponse                = barsServiceResponse.futureValue
    httpResponse
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
        .withHeader(HeaderNames.USER_AGENT, equalTo("calculate-public-pension-adjustment"))
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
}
