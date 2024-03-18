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
import models.calculation.response.CalculationResponse
import models.submission.Submission
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.io.Source

class CalculationResultControllerSpec extends SpecBase {

  "CalculationResult Controller" - {

    "must return OK with includeCompensation2015 and return the correct view for a GET" in {

      val calculationResult: CalculationResponse =
        readCalculationResult("test/resources/CalculationResultsTestData.json")

      val submission = Submission("", "", calculationInputs, Option.apply(calculationResult))

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers), submission = Some(submission)).build()

      running(application) {
        val request = FakeRequest(GET, routes.CalculationResultController.onPageLoad().url)

        val result = route(application, request).value

        status(result) mustEqual OK
        contentAsString(result).contains("Calculation results") mustBe true
        contentAsString(result).contains("6 April 2015 to 5 April 2019 does include compensation") mustBe true
      }
    }
  }

  def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }
}
