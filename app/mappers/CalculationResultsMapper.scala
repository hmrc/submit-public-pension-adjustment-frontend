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

import models.calculation.{CalculationResultsViewModel, RowViewModel}
import models.calculation.response.CalculationResponse

object CalculationResultsMapper {
  def calculationResultsViewModel(calculationResponse: CalculationResponse): CalculationResultsViewModel = {
    val resubmissionVal: Seq[RowViewModel]  = resubmission(calculationResponse)
    val totalAmountVal: Seq[RowViewModel]   = totalAmount(calculationResponse)
    val outDatesVal: Seq[Seq[RowViewModel]] = outDates(calculationResponse)
    val inDatesVal: Seq[Seq[RowViewModel]]  = inDates(calculationResponse)
    CalculationResultsViewModel(totalAmountVal, resubmissionVal, outDatesVal, inDatesVal)
  }

  private def totalAmount(calculateResponse: CalculationResponse): Seq[RowViewModel] =
    Seq(
      RowViewModel(
        "calculationResults.outDatesCompensation",
        calculateResponse.totalAmounts.outDatesCompensation.toString()
      )
    ) ++
      Seq(RowViewModel("calculationResults.inDatesDebit", calculateResponse.totalAmounts.inDatesDebit.toString())) ++
      Seq(RowViewModel("calculationResults.inDatesCredit", calculateResponse.totalAmounts.inDatesCredit.toString()))

  private def resubmission(calculateResponse: CalculationResponse): Seq[RowViewModel] =
    if (calculateResponse.resubmission.isResubmission) {
      Seq(RowViewModel("calculationResults.annualResults.isResubmission", "")) ++
        Seq(
          RowViewModel("calculationResults.annualResults.reason", calculateResponse.resubmission.reason.getOrElse(""))
        )
    } else {
      Seq(RowViewModel("calculationResults.annualResults.notResubmission", ""))
    }

  private def outDates(calculateResponse: CalculationResponse): Seq[Seq[RowViewModel]] =
    calculateResponse.outDates.map { outDate =>
      Seq(
        RowViewModel("periodDateRangeAA." + outDate.period.toString, outDate.period.toString),
        RowViewModel("calculationResults.annualResults.chargePaidBySchemes", outDate.chargePaidBySchemes.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidByMember", outDate.chargePaidByMember.toString()),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate",
          outDate.revisedChargableAmountAfterTaxRate.toString()
        ),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate",
          outDate.revisedChargableAmountBeforeTaxRate.toString()
        ),
        RowViewModel("calculationResults.annualResults.directCompensation", outDate.directCompensation.toString()),
        RowViewModel("calculationResults.annualResults.indirectCompensation", outDate.indirectCompensation.toString()),
        RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", outDate.unusedAnnualAllowance.toString())
      )
    }

  private def inDates(calculateResponse: CalculationResponse): Seq[Seq[RowViewModel]] =
    calculateResponse.inDates.map { inDate =>
      Seq(
        RowViewModel("periodDateRangeAA." + inDate.period.toString(), inDate.period.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidBySchemes", inDate.chargePaidBySchemes.toString()),
        RowViewModel("calculationResults.annualResults.chargePaidByMember", inDate.chargePaidByMember.toString()),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountAfterTaxRate",
          inDate.revisedChargableAmountAfterTaxRate.toString()
        ),
        RowViewModel(
          "calculationResults.annualResults.revisedChargeableAmountBeforeTaxRate",
          inDate.revisedChargableAmountBeforeTaxRate.toString()
        ),
        RowViewModel("calculationResults.annualResults.memberCredit", inDate.memberCredit.toString()),
        RowViewModel("calculationResults.annualResults.schemeCredit", inDate.schemeCredit.toString()),
        RowViewModel("calculationResults.annualResults.debit", inDate.debit.toString()),
        RowViewModel("calculationResults.annualResults.unusedAnnualAllowance", inDate.unusedAnnualAllowance.toString())
      )
    }
}
