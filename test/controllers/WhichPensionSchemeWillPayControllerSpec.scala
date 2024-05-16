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
import forms.WhichPensionSchemeWillPayFormProvider
import models.calculation.inputs.Income.AboveThreshold
import models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
import models.calculation.inputs.CalculationInputs
import models.calculation.response.{CalculationResponse, TaxYearScheme}
import models.submission.Submission
import models.{Done, NormalMode, UserAnswers, WhichPensionSchemeWillPay}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhichPensionSchemeWillPayPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserDataService
import views.html.WhichPensionSchemeWillPayView

import scala.concurrent.Future

class WhichPensionSchemeWillPayControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichPensionSchemeWillPayRoute =
    routes.WhichPensionSchemeWillPayController.onPageLoad(NormalMode, models.Period._2020).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val formProvider = new WhichPensionSchemeWillPayFormProvider()
  val form         = formProvider()

  "WhichPensionSchemeWillPay Controller" - {

    "must return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichPensionSchemeWillPayView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form,
          NormalMode,
          models.Period._2020,
          WhichPensionSchemeWillPay(Seq("Private pension scheme"))
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhichPensionSchemeWillPayPage(models.Period._2020), "Scheme1 / 00348916RT")
        .success
        .value

      val mockCalculationInputs = CalculationInputs(
        models.calculation.inputs.Resubmission(false, None),
        Option(
          models.calculation.inputs.AnnualAllowance(
            List(),
            List(
              NormalTaxYear(
                2,
                List(TaxYearScheme("Scheme1", "00348916RT", 2, 0, Some(4))),
                5,
                0,
                models.calculation.inputs.Period._2016,
                None
              ),
              NormalTaxYear(
                5,
                List(TaxYearScheme("Scheme1", "00348916RT", 5, 7, None)),
                8,
                6,
                models.calculation.inputs.Period._2017,
                Some(AboveThreshold(7))
              )
            )
          )
        ),
        None
      )

      val mockCalculationResponse = mock[CalculationResponse]

      val submission: Submission =
        Submission("id", "submissionUniqueId", mockCalculationInputs, Option(mockCalculationResponse))

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayRoute)

        val view = application.injector.instanceOf[WhichPensionSchemeWillPayView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill("Scheme1 / 00348916RT"),
          NormalMode,
          models.Period._2020,
          WhichPensionSchemeWillPay(Seq("Scheme1 / 00348916RT", "Private pension scheme"))
        )(
          request,
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
        val request =
          FakeRequest(POST, whichPensionSchemeWillPayRoute)
            .withFormUrlEncodedBody(("value", "Scheme1_PSTR"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichPensionSchemeWillPayRoute)
            .withFormUrlEncodedBody(("value", "Scheme1_PSTR"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
