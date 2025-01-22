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
import forms.MemberDateOfDeathFormProvider
import models.{Done, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.MemberDateOfDeathPage
import play.api.i18n.Messages
import play.api.inject.bind
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Call}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.MemberDateOfDeathView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class MemberDateOfDeathControllerSpec extends SpecBase with MockitoSugar {

  val formProvider                        = new MemberDateOfDeathFormProvider(clockAtFixedInstant)
  private implicit val messages: Messages = stubMessages()

  private def form = formProvider()(messages)

  def onwardRoute = Call("GET", "/foo")

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  lazy val memberDateOfDeathRoute = routes.MemberDateOfDeathController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  override val emptyUserAnswers = UserAnswers(userAnswersId)

  def getRequest(): FakeRequest[AnyContentAsEmpty.type] =
    FakeRequest(GET, memberDateOfDeathRoute)

  def postRequest(): FakeRequest[AnyContentAsFormUrlEncoded] =
    FakeRequest(POST, memberDateOfDeathRoute)
      .withFormUrlEncodedBody(
        "value.day"   -> validAnswer.getDayOfMonth.toString,
        "value.month" -> validAnswer.getMonthValue.toString,
        "value.year"  -> validAnswer.getYear.toString
      )

  "MemberDateOfDeath Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val result = route(application, getRequest()).value

        val view = application.injector.instanceOf[MemberDateOfDeathView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(getRequest(), messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(MemberDateOfDeathPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val view = application.injector.instanceOf[MemberDateOfDeathView]

        val result = route(application, getRequest()).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form.fill(validAnswer), NormalMode)(
          getRequest(),
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.PensionSchemeMemberNinoController
          .onPageLoad(NormalMode)
          .url
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      val request =
        FakeRequest(POST, memberDateOfDeathRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      running(application) {
        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[MemberDateOfDeathView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val result = route(application, getRequest()).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val result = route(application, postRequest).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, memberDateOfDeathRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
