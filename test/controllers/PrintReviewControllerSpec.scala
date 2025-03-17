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
import models.calculation.inputs.CalculationInputs
import models.calculation.response.CalculationResponse
import models.submission.Submission
import pages.TestData
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.*

import scala.io.Source

class PrintReviewControllerSpec extends SpecBase {

  "PrintReview Controller" - {

    "must redirect to view LTA answers when LTA only" in {

      val calculationInputs: CalculationInputs = TestData.calculationInputsLTAOnly

      val submission = Submission("", "", calculationInputs, None)

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.PrintReviewController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` SEE_OTHER
        redirectLocation(result).value `mustEqual` controllers.routes.ViewYourLTAAnswersController.onPageLoad().url
      }
    }

    "must return OK with includeCompensation2015 and return the correct view for a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val calculationInputs: CalculationInputs = TestData.calculationInputs

      val submission = Submission("", "", calculationInputs, Option.apply(calculationResult))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.PrintReviewController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result).contains("Overview of calculation results") `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) `mustBe` true
      }
    }

    "when only out of dates AA, only show content relevant for out dates" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsOutDatesTestData.json")

      val calculationInputs: CalculationInputs = TestData.calculationInputs

      val submission = Submission("", "", calculationInputs, Option.apply(calculationResult))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.PrintReviewController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result).contains("Overview of calculation results") `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) `mustBe` false
      }
    }

    "when LTA reported, should show content relevant for LTA" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val calculationInputs: CalculationInputs = TestData.calculationInputs

      val submission = Submission("", "", calculationInputs, Option.apply(calculationResult))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.PrintReviewController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result).contains("Overview of calculation results") `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) `mustBe` true
        contentAsString(result).contains("Lifetime allowance answers") `mustBe` true

      }
    }

    "when no LTA reported, should not show content for LTA" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val calculationInputsNoLta: CalculationInputs = TestData.calculationInputs.copy(
        lifeTimeAllowance = None
      )

      val submission = Submission("", "", calculationInputsNoLta, Option.apply(calculationResult))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.PrintReviewController.onPageLoad().url)

        val result = route(application, request).value

        status(result) `mustEqual` OK
        contentAsString(result).contains("Overview of calculation results") `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2015 to 5 April 2019"
        ) `mustBe` true
        contentAsString(result).contains(
          "Change in annual allowance tax charges from 6 April 2019 to 5 April 2023"
        ) `mustBe` true
        contentAsString(result).contains("Lifetime allowance answers") `mustBe` false

      }
    }

  }

  def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }
}
