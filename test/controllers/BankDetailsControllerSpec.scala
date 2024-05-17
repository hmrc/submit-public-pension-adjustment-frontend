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

import bars.PpaBarsService
import bars.barsmodel.response._
import base.SpecBase
import forms.BankDetailsFormProvider
import models.{BankDetails, Done, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.BankDetailsPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.BankDetailsView

import scala.concurrent.Future
import scala.reflect.ClassTag

class BankDetailsControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new BankDetailsFormProvider()
  val form         = formProvider()

  lazy val createDDRoute = routes.BankDetailsController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      BankDetailsPage.toString -> Json.obj(
        "accountName"   -> "theAccountName",
        "sortCode"      -> "123456",
        "accountNumber" -> "12345678"
      )
    )
  )

  "CreateDD Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, createDDRoute)

        val view = application.injector.instanceOf[BankDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, createDDRoute)

        val view = application.injector.instanceOf[BankDetailsView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(BankDetails("theAccountName", "123456", "12345678")),
          NormalMode
        )(request, messages(application)).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsASuccessResponse()

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(
            bind[UserDataService].toInstance(mockUserDataService),
            bind[PpaBarsService].toInstance(mcCloudBarsServiceMock)
          )
          .build()

      running(application) {
        val request =
          FakeRequest(POST, createDDRoute)
            .withFormUrlEncodedBody(
              ("accountName", "theAccountName"),
              ("sortCode", "123456"),
              ("accountNumber", "12341234"),
              ("buildingNumber", "1")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.CheckYourAnswersController.onPageLoad.url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, createDDRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[BankDetailsView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, createDDRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, createDDRoute)
            .withFormUrlEncodedBody(("AcountNme", "value 1"), ("SortCde", "value 2"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    // BARS ERROR CONDITIONS

    "must show an error when the sort code is on the deny list" in {

      val errorResponseType                      = SortCodeOnDenyListErrorResponse(SortCodeOnDenyList(BarsErrorResponse("code", "desc")))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when the account name does not match" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = NameDoesNotMatch(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   =
        "Enter the name on the account as it appears on bank statements. Do not copy and paste it"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when bank account details are invalid" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = AccountNumberNotWellFormatted(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when SortCodeNotPresentOnEiscd" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = SortCodeNotPresentOnEiscd(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when ThirdPartyError" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = ThirdPartyError(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "BARS verify third-party error. BARS response:"

      assertThrowsWithMessage[RuntimeException](expectedErrorMessage) {
        submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, "")
      }
    }

    "must show an error when AccountDoesNotExist" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = AccountDoesNotExist(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when OtherBarsError" in {

      val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

      val errorResponseType: BarsVerifyError     = OtherBarsError(VerifyResponse(barsVerifyResponse))
      val mcCloudBarsServiceMock: PpaBarsService = whenTheBarsServiceReturnsAnErrorResponse(errorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must show an error when no other error caught" in {
      val unexpectedErrorResponseType: BarsVerifyError = mock[BarsVerifyError] // This is a new unexpected error type

      val mcCloudBarsServiceMock: PpaBarsService =
        whenTheBarsServiceReturnsAnUnexpectedErrorResponse(unexpectedErrorResponseType)
      val expectedErrorMessage                   = "Enter a valid combination of bank account number and sort code"

      submitFormAndCheckErrorMessage(mcCloudBarsServiceMock, expectedErrorMessage)
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, createDDRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }

  }

  private def submitFormAndCheckErrorMessage(mcCloudBarsServiceMock: PpaBarsService, expectedErrorMessage: String) = {
    val mockUserDataService = mock[UserDataService]

    when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

    val application =
      applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
        .overrides(
          bind[UserDataService].toInstance(mockUserDataService),
          bind[PpaBarsService].toInstance(mcCloudBarsServiceMock)
        )
        .build()

    running(application) {
      val request =
        FakeRequest(POST, createDDRoute)
          .withFormUrlEncodedBody(
            ("accountName", "theAccountName"),
            ("sortCode", "123456"),
            ("accountNumber", "12341234")
          )

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST
      val messageFound = contentAsString(result) contains expectedErrorMessage
      messageFound mustEqual true
    }
  }

  private def whenTheBarsServiceReturnsASuccessResponse() = {
    val mcCloudBarsServiceMock = mock[PpaBarsService]

    val barsVerifyResponse: BarsVerifyResponse = aBarsVerifyResponse

    val successfulVerifyResponse = Right(VerifyResponse(barsVerifyResponse))

    when(mcCloudBarsServiceMock.verifyBankDetails(any())(any(), any())) thenReturn Future.successful(
      successfulVerifyResponse
    )
    mcCloudBarsServiceMock
  }

  private def aBarsVerifyResponse = {
    val barsVerifyResponse = BarsVerifyResponse(
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      BarsAssessmentType.Yes,
      None,
      None,
      None
    )
    barsVerifyResponse
  }

  private def whenTheBarsServiceReturnsAnErrorResponse(errorResponseType: BarsPreVerifyError) = {
    val mcCloudBarsServiceMock = mock[PpaBarsService]

    val errorResponse = Left(errorResponseType)
    when(mcCloudBarsServiceMock.verifyBankDetails(any())(any(), any())) thenReturn Future.successful(errorResponse)
    mcCloudBarsServiceMock
  }

  private def whenTheBarsServiceReturnsAnErrorResponse(errorResponseType: BarsVerifyError) = {
    val mcCloudBarsServiceMock = mock[PpaBarsService]

    val errorResponse = Left(errorResponseType)
    when(mcCloudBarsServiceMock.verifyBankDetails(any())(any(), any())) thenReturn Future.successful(errorResponse)
    mcCloudBarsServiceMock
  }

  def assertThrowsWithMessage[T <: Throwable](
    expectedMessageStart: String
  )(block: => Unit)(implicit classTag: ClassTag[T]): Unit = {
    val thrown = intercept[T] {
      block
    }
    assert(thrown.getMessage.startsWith(expectedMessageStart))
  }

  private def whenTheBarsServiceReturnsAnUnexpectedErrorResponse(errorResponseType: BarsVerifyError) = {
    val mcCloudBarsServiceMock = mock[PpaBarsService]

    val errorResponse = Left(errorResponseType)
    when(mcCloudBarsServiceMock.verifyBankDetails(any())(any(), any())) thenReturn Future.successful(errorResponse)
    mcCloudBarsServiceMock
  }

}
