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

package controllers

import base.SpecBase
import forms.WhichPensionSchemeWillPayTaxReliefFormProvider
import models.calculation.inputs.{AnnualAllowance, CalculationInputs, Period, TaxYear}
import models.calculation.response.{CalculationResponse, TaxYearScheme, TotalAmounts}
import models.submission.Submission
import models.{NormalMode, UserAnswers, WhichPensionSchemeWillPayTaxRelief}
import org.mockito.ArgumentMatchers.any
import models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
import models.calculation.inputs.Income.AboveThreshold
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.WhichPensionSchemeWillPayTaxReliefPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.WhichPensionSchemeWillPayTaxReliefView

import scala.concurrent.Future

class WhichPensionSchemeWillPayTaxReliefControllerSpec extends SpecBase with MockitoSugar {

  def onwardRoute = Call("GET", "/foo")

  lazy val whichPensionSchemeWillPayTaxReliefRoute =
    routes.WhichPensionSchemeWillPayTaxReliefController.onPageLoad(NormalMode).url

  lazy val calculationPrerequisiteRoute = routes.CalculationPrerequisiteController.onPageLoad().url

  val formProvider = new WhichPensionSchemeWillPayTaxReliefFormProvider()
  val form         = formProvider()

  "WhichPensionSchemeWillPayTaxRelief Controller" - {

    "must return OK and the correct view for a GET" in {

      val mockCalculationInputs = CalculationInputs(
        models.calculation.inputs.Resubmission(false, None),
        Option(
          models.calculation.inputs.AnnualAllowance(
            List(),
            List(
              NormalTaxYear(
                2,
                List(TaxYearScheme("Scheme1", "00348916RT", 1, 2, 0)),
                5,
                0,
                Period._2016PreAlignment,
                None
              ),
              NormalTaxYear(
                4,
                List(TaxYearScheme("Scheme1", "00348916RT", 3, 4, 0)),
                5,
                0,
                Period._2016PostAlignment,
                None
              ),
              NormalTaxYear(
                5,
                List(TaxYearScheme("Scheme1", "00348916RT", 4, 5, 7)),
                8,
                6,
                Period._2017,
                Some(AboveThreshold(7))
              )
            )
          )
        ),
        None
      )

      val mockCalculationResponse = mock[CalculationResponse]

      val submission: Submission =
        Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Option(mockCalculationResponse))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayTaxReliefRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[WhichPensionSchemeWillPayTaxReliefView]

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form,
          NormalMode,
          WhichPensionSchemeWillPayTaxRelief(Seq("Scheme1 / 00348916RT"))
        )(request, messages(application)).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId)
        .set(WhichPensionSchemeWillPayTaxReliefPage, "Scheme1 / 00348916RT")
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
                List(TaxYearScheme("Scheme1", "00348916RT", 1, 2, 0)),
                5,
                0,
                Period._2016PreAlignment,
                None
              ),
              NormalTaxYear(
                4,
                List(TaxYearScheme("Scheme1", "00348916RT", 3, 4, 0)),
                5,
                0,
                Period._2016PostAlignment,
                None
              ),
              NormalTaxYear(
                5,
                List(TaxYearScheme("Scheme1", "00348916RT", 4, 5, 7)),
                8,
                6,
                Period._2017,
                Some(AboveThreshold(7))
              )
            )
          )
        ),
        None
      )

      val mockCalculationResponse = mock[CalculationResponse]

      val submission: Submission =
        Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Option(mockCalculationResponse))

      val application = applicationBuilder(userAnswers = Some(userAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayTaxReliefRoute)

        val view = application.injector.instanceOf[WhichPensionSchemeWillPayTaxReliefView]

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result) mustEqual view(
          form.fill("Scheme1 / 00348916RT"),
          NormalMode,
          WhichPensionSchemeWillPayTaxRelief(Seq("Scheme1 / 00348916RT"))
        )(
          request,
          messages(application)
        ).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {

      val mockSessionRepository = mock[SessionRepository]

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val period: models.calculation.response.Period = models.calculation.response.Period._2021

      val mockCalculationInputs = mock[CalculationInputs]

      val calculationResponse    = CalculationResponse(
        models.calculation.response.Resubmission(false, None),
        TotalAmounts(0, 1, 0),
        List.empty,
        List(models.calculation.response.InDatesTaxYearsCalculation(period, 320, 0, 0, 0, 0, 0, 0, 0, List.empty))
      )
      val submission: Submission =
        Submission("sessionId", "submissionUniqueId", mockCalculationInputs, Some(calculationResponse))

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .build()

      running(application) {
        val request =
          FakeRequest(POST, whichPensionSchemeWillPayTaxReliefRoute)
            .withFormUrlEncodedBody(("value", "Scheme1 / 00348916RT"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.BankDetailsController.onPageLoad(NormalMode).url
      }
    }

    "must redirect to Journey Recovery for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayTaxReliefRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "redirect to Journey Recovery for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None, submission = Some(submission)).build()

      running(application) {
        val request =
          FakeRequest(POST, whichPensionSchemeWillPayTaxReliefRoute)
            .withFormUrlEncodedBody(("value", "Scheme1 / 00348916RT"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.JourneyRecoveryController.onPageLoad().url
      }
    }

    "must redirect to Calculation Prerequisite for a GET if no submission data is found" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      running(application) {
        val request = FakeRequest(GET, whichPensionSchemeWillPayTaxReliefRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual calculationPrerequisiteRoute
      }
    }
  }
}
