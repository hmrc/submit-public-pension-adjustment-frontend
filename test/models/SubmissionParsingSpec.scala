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

package models

import models.calculation.inputs.Income.AboveThreshold
import models.calculation.inputs.TaxYear2016To2023.NormalTaxYear
import models.calculation.inputs.{AnnualAllowance, Period}
import models.calculation.response.{TaxYearScheme, TotalAmounts}
import models.submission.RetrieveSubmissionResponse
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

class SubmissionParsingSpec extends AnyFreeSpec with Matchers {

  "Data model" - {

    "must parse a submission in alignment with calculate backend" in {
      val res: RetrieveSubmissionResponse = readRetrieveSubmissionResponse("test/resources/Submission.json")

      res.calculationInputs.resubmission      must be(models.calculation.inputs.Resubmission(false, None))
      res.calculationInputs.annualAllowance   must be(
        Some(
          AnnualAllowance(
            List(),
            List(
              NormalTaxYear(2, List(TaxYearScheme("1", "00348916RT", 1, 2, 0)), 5, 0, Period._2016PreAlignment, None),
              NormalTaxYear(4, List(TaxYearScheme("1", "00348916RT", 3, 4, 0)), 5, 0, Period._2016PostAlignment, None),
              NormalTaxYear(
                5,
                List(TaxYearScheme("1", "00348916RT", 4, 5, 7)),
                8,
                6,
                Period._2017,
                Some(AboveThreshold(7))
              )
            )
          )
        )
      )
      res.calculationInputs.lifeTimeAllowance must be(None)

      val calculation = res.calculation.get

      calculation.resubmission  must be(models.calculation.response.Resubmission(false, None))
      calculation.totalAmounts  must be(TotalAmounts(13, 0, 0))
      calculation.inDates.size  must be(0)
      calculation.outDates.size must be(3)
    }
  }

  def readRetrieveSubmissionResponse(calculationResponseFile: String): RetrieveSubmissionResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[RetrieveSubmissionResponse]
  }
}
