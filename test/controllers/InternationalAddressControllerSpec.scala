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
import forms.InternationalAddressFormProvider
import models.calculation.response.TaxYearScheme
import models.submission.Submission
import models.{Done, InternationalAddress, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.InternationalAddressPage
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.InternationalAddressView

import scala.concurrent.Future

class InternationalAddressControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new InternationalAddressFormProvider()
  val form         = formProvider()

  lazy val internationalAddressRoute = routes.InternationalAddressController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val userAnswers = UserAnswers(
    userAnswersId,
    Json.obj(
      InternationalAddressPage.toString -> Json.obj(
        "addressLine1"  -> "value 1",
        "addressLine2"  -> "value 2",
        "townOrCity"    -> "townOrCity",
        "stateOrRegion" -> "stateOrRegion",
        "postCode"      -> "postCode",
        "country"       -> "country"
      )
    )
  )

  "InternationalAddress Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, internationalAddressRoute)

        val view = application.injector.instanceOf[InternationalAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(form, NormalMode)(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, internationalAddressRoute)

        val view = application.injector.instanceOf[InternationalAddressView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill(
            InternationalAddress(
              "value 1",
              Some("value 2"),
              "townOrCity",
              Some("stateOrRegion"),
              Some("postCode"),
              "country"
            )
          ),
          NormalMode
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockUserDataService = mock[UserDataService]

      when(mockUserDataService.set(any())(any())) thenReturn Future.successful(Done)

      val submission: Submission =
        submissionRelatingToTaxYearSchemes(List(TaxYearScheme("scheme1", "12345678AB", 0, 0, None)))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(bind[UserDataService].toInstance(mockUserDataService))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, internationalAddressRoute)
            .withFormUrlEncodedBody(
              ("addressLine1", "value 1"),
              ("addressLine2", "value 2"),
              ("townOrCity", "townOrCity"),
              ("stateOrRegion", "stateOrRegion"),
              ("postCode", "postCode"),
              ("country", "country")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, internationalAddressRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[InternationalAddressView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST
        contentAsString(result) mustEqual view(boundForm, NormalMode)(request, messages(application)).toString
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, internationalAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, internationalAddressRoute)
            .withFormUrlEncodedBody(
              ("addressLine1", "value 1"),
              ("addressLine2", "value 2"),
              ("townOrCity", "townOrCity"),
              ("stateOrRegion", "stateOrRegion"),
              ("postCode", "postCode"),
              ("country", "country")
            )

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, internationalAddressRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
