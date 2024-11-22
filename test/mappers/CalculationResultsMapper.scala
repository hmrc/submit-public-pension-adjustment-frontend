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

package mappers

import base.SpecBase
import models.Period
import models.calculation.inputs.CalculationInputs
import models.calculation.response.CalculationResponse
import models.calculation.{CalculationResultsViewModel, CalculationReviewIndividualAAViewModel, RowViewModel}
import org.mockito.MockitoSugar
import pages.TestData
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

class CalculationResultsMapperSpec extends SpecBase with MockitoSugar{

  implicit lazy val headerCarrier: HeaderCarrier = HeaderCarrier()

  private def readCalculationResult(calculationResponseFile: String): CalculationResponse = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[CalculationResponse]
  }

  def checkYearInDates(year: Seq[RowViewModel]) = {
    year.size mustBe 9

    year(0).value mustNot be(null)
    checkRowName(year, 1, "calculationResults.annualResults.chargePaidBySchemes")
    checkRowName(year, 2, "calculationResults.annualResults.chargePaidByMember")
    checkRowName(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate")
    checkRowName(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate")
    checkRowName(year, 5, "calculationResults.annualResults.memberCredit")
    checkRowName(year, 6, "calculationResults.annualResults.schemeCredit")
    checkRowName(year, 7, "calculationResults.annualResults.debit")
    checkRowName(year, 8, "calculationResults.annualResults.unusedAnnualAllowance")
  }

  def checkYearOutDates(year: Seq[RowViewModel]) = {
    year.size mustBe 8

    year(0).value mustNot be(null)
    checkRowName(year, 1, "calculationResults.annualResults.chargePaidBySchemes")
    checkRowName(year, 2, "calculationResults.annualResults.chargePaidByMember")
    checkRowName(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate")
    checkRowName(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate")
    checkRowName(year, 5, "calculationResults.annualResults.directCompensation")
    checkRowName(year, 6, "calculationResults.annualResults.indirectCompensation")
    checkRowName(year, 7, "calculationResults.annualResults.unusedAnnualAllowance")
  }

  def checkRowNameAndValue(rows: Seq[RowViewModel], index: Int, expectedName: String, expectedValue: String): Unit = {
    rows(index).name mustBe expectedName
    rows(index).value mustBe expectedValue
  }

  def checkRowName(rows: Seq[RowViewModel], index: Int, expectedName: String): Unit = {
    rows(index).name mustBe expectedName
    rows(index).value mustNot be(null)
  }


  "CalculationResultsMapper" - {
    "resubmission details should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        CalculationResultsMapper.calculationResultsViewModel(calculationResult)

      val rows: Seq[RowViewModel] = viewModel.resubmissionVal

      rows.size mustBe 2
      checkRowNameAndValue(rows, 0, "calculationResults.annualResults.isResubmission", "")
      checkRowNameAndValue(rows, 1, "calculationResults.annualResults.reason", "Change in amounts")

      viewModel.resubmissionData mustBe List(
        List(
          RowViewModel("calculationResults.annualResults.isResubmission", ""),
          RowViewModel("calculationResults.annualResults.reason", "Change in amounts")
        )
      )
    }

    "total amounts should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        CalculationResultsMapper.calculationResultsViewModel(calculationResult)

      val rows: Seq[RowViewModel] = viewModel.totalAmounts

      rows.size mustBe 3
      checkRowNameAndValue(rows, 0, "calculationResults.outDatesCompensation", "8400")
      checkRowNameAndValue(rows, 1, "calculationResults.inDatesDebit", "0")
      checkRowNameAndValue(rows, 2, "calculationResults.inDatesCredit", "0")

      viewModel.calculationData mustBe List(
        List(
          RowViewModel("calculationResults.outDatesCompensation", "8400"),
          RowViewModel("calculationResults.inDatesDebit", "0"),
          RowViewModel("calculationResults.inDatesCredit", "0")
        )
      )
    }

    "all years in out dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        CalculationResultsMapper.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.outDates

      sections.foreach(year => checkYearOutDates(year))
    }

    "in dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        CalculationResultsMapper.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.inDates
      sections.size mustBe 4

      val year = sections(0)

      checkRowNameAndValue(year, 0, "periodDateRangeAA.2020", "2020")
      checkRowNameAndValue(year, 1, "calculationResults.annualResults.chargePaidBySchemes", "0")
      checkRowNameAndValue(year, 2, "calculationResults.annualResults.chargePaidByMember", "0")
      checkRowNameAndValue(year, 3, "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate", "0")
      checkRowNameAndValue(year, 4, "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate", "0")
      checkRowNameAndValue(year, 5, "calculationResults.annualResults.memberCredit", "0")
      checkRowNameAndValue(year, 6, "calculationResults.annualResults.schemeCredit", "0")
      checkRowNameAndValue(year, 7, "calculationResults.annualResults.debit", "0")
      checkRowNameAndValue(year, 8, "calculationResults.annualResults.unusedAnnualAllowance", "48000")

    }

    "all years in in-dates should be well formed" in {
      val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

      val viewModel: CalculationResultsViewModel =
        CalculationResultsMapper.calculationResultsViewModel(calculationResult)

      val sections: Seq[Seq[RowViewModel]] = viewModel.inDates

      sections.foreach(year => checkYearInDates(year))
    }

    "Caculation Reivew Individual AA" - {

      "out dates Review AA should be well formed and should filter chosen period when given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val calculationInputs: CalculationInputs = TestData.calculationInputs

        val viewModel: CalculationReviewIndividualAAViewModel =
          CalculationResultsMapper.calculationReviewIndividualAAViewModel(
            calculationResult,
            Some(Period._2016.toString()),
            calculationInputs
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.outDates
        sections.size mustBe 1

        val year = sections(0)

        checkRowNameAndValue(year, 0, "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember", "£0")
        checkRowNameAndValue(year, 1, "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          year,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          year,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          year,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          year,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          year,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "£60,000"
        )
        checkRowNameAndValue(
          year,
          7,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          year,
          8,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          year,
          9,
          "calculationReviewIndividualAA.annualResults.outDates.amountYouOwe",
          "£0"
        )
        checkRowNameAndValue(
          year,
          10,
          "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
          "£0"
        )
        checkRowNameAndValue(
          year,
          11,
          "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
          "£0"
        )

      }

      "in dates Review AA should be well formed and should filter chosen period when given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val calculationInputs: CalculationInputs = TestData.calculationInputs

        val viewModel: CalculationReviewIndividualAAViewModel =
          CalculationResultsMapper.calculationReviewIndividualAAViewModel(
            calculationResult,
            Some(Period._2020.toString()),
            calculationInputs
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.inDates
        sections.size mustBe 1

        val year = sections(0)
        checkRowNameAndValue(year, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "£0")
        checkRowNameAndValue(year, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          year,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          year,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          year,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          year,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "£2,189"
        )
        checkRowNameAndValue(
          year,
          6,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "£48,000"
        )
        checkRowNameAndValue(
          year,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          year,
          8,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          year,
          9,
          "calculationReviewIndividualAA.annualResults.inDates.debit",
          "£0"
        )
        checkRowNameAndValue(
          year,
          10,
          "calculationReviewIndividualAA.annualResults.inDates.memberCredit",
          "£0"
        )
        checkRowNameAndValue(
          year,
          11,
          "calculationReviewIndividualAA.annualResults.inDates.schemeCredit",
          "£0"
        )

      }

      "out dates Review AA should be well formed and should return all period when NOT given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val calculationInputs: CalculationInputs = TestData.calculationInputs

        val viewModel: CalculationReviewIndividualAAViewModel =
          CalculationResultsMapper.calculationReviewIndividualAAViewModel(
            calculationResult,
            None,
            calculationInputs
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.outDates
        sections.size mustBe 4

        val _2016Period = sections(0)
        val _2017Period = sections(1)

        checkRowNameAndValue(_2016Period, 0, "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember", "£0")
        checkRowNameAndValue(_2016Period, 1, "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          _2016Period,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          _2016Period,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          _2016Period,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          _2016Period,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          _2016Period,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "£60,000"
        )
        checkRowNameAndValue(
          _2016Period,
          7,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2016Period,
          8,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2016Period,
          9,
          "calculationReviewIndividualAA.annualResults.outDates.amountYouOwe",
          "£0"
        )
        checkRowNameAndValue(
          _2016Period,
          10,
          "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
          "£0"
        )
        checkRowNameAndValue(
          _2016Period,
          11,
          "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
          "£0"
        )

        checkRowNameAndValue(_2017Period, 0, "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember", "£1,200")
        checkRowNameAndValue(_2017Period, 1, "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          _2017Period,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          _2017Period,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          _2017Period,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          _2017Period,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "£2,189"
        )
        checkRowNameAndValue(
          _2017Period,
          6,
          "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
          "£0"
        )
        checkRowNameAndValue(
          _2017Period,
          7,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2017Period,
          8,
          "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2017Period,
          9,
          "calculationReviewIndividualAA.annualResults.outDates.amountYouOwe",
          "£0"
        )
        checkRowNameAndValue(
          _2017Period,
          10,
          "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
          "£1,200"
        )
        checkRowNameAndValue(
          _2017Period,
          11,
          "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
          "£0"
        )


      }

      "in dates Review AA should be well formed and should return all period when NOT given period" in {
        val calculationResult = readCalculationResult("test/resources/CalculationResultsTestData.json")

        val calculationInputs: CalculationInputs = TestData.calculationInputs

        val viewModel: CalculationReviewIndividualAAViewModel =
          CalculationResultsMapper.calculationReviewIndividualAAViewModel(
            calculationResult,
            None,
            calculationInputs
          )

        val sections: Seq[Seq[RowViewModel]] = viewModel.inDates
        sections.size mustBe 4

        val _2020 = sections(0)
        val _2021 = sections(1)

        println(_2021)
        checkRowNameAndValue(_2020, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "£0")
        checkRowNameAndValue(_2020, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          _2020,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          _2020,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          _2020,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          _2020,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "£2,189"
        )
        checkRowNameAndValue(
          _2020,
          6,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "£48,000"
        )
        checkRowNameAndValue(
          _2020,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2020,
          8,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2020,
          9,
          "calculationReviewIndividualAA.annualResults.inDates.debit",
          "£0"
        )
        checkRowNameAndValue(
          _2020,
          10,
          "calculationReviewIndividualAA.annualResults.inDates.memberCredit",
          "£0"
        )
        checkRowNameAndValue(
          _2020,
          11,
          "calculationReviewIndividualAA.annualResults.inDates.schemeCredit",
          "£0"
        )

        checkRowNameAndValue(_2021, 0, "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember", "£0")
        checkRowNameAndValue(_2021, 1, "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes", "£0")
        checkRowNameAndValue(
          _2021,
          2,
          "calculationReviewIndividualAA.annualResults.reducedNetIncome",
          "£0"
        )
        checkRowNameAndValue(
          _2021,
          3,
          "calculationReviewIndividualAA.annualResults.personalAllowance",
          "£4,573"
        )
        checkRowNameAndValue(
          _2021,
          4,
          "calculationReviewIndividualAA.annualResults.thresholdIncome",
          "site.notApplicable"
        )
        checkRowNameAndValue(
          _2021,
          5,
          "calculationReviewIndividualAA.annualResults.adjustedIncome",
          "£2,189"
        )
        checkRowNameAndValue(
          _2021,
          6,
          "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
          "£56,000"
        )
        checkRowNameAndValue(
          _2021,
          7,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2021,
          8,
          "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
          "£0"
        )
        checkRowNameAndValue(
          _2021,
          9,
          "calculationReviewIndividualAA.annualResults.inDates.debit",
          "£0"
        )
        checkRowNameAndValue(
          _2021,
          10,
          "calculationReviewIndividualAA.annualResults.inDates.memberCredit",
          "£0"
        )
        checkRowNameAndValue(
          _2021,
          11,
          "calculationReviewIndividualAA.annualResults.inDates.schemeCredit",
          "£0"
        )

      }


    }


  }

}
