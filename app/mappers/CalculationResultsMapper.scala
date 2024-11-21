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

import models.calculation.inputs.TaxYear2016To2023.{InitialFlexiblyAccessedTaxYear, NormalTaxYear, PostFlexiblyAccessedTaxYear}
import models.calculation.inputs.{CalculationInputs, IncomeSubJourney, TaxYear2016To2023}
import models.calculation.{CalculationResultsViewModel, CalculationReviewIndividualAAViewModel, CalculationReviewViewModel, IndividualAASummaryModel, ReviewRowViewModel, RowViewModel}
import models.calculation.response.{CalculationResponse, InDatesTaxYearsCalculation, OutOfDatesTaxYearsCalculation, Period, Resubmission}
import uk.gov.hmrc.http.HeaderCarrier
import utils.CurrencyFormatter.currencyFormat

import scala.concurrent.ExecutionContext

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

  private def lifetimeAllowanceReview: Seq[ReviewRowViewModel] =
    Seq(
      ReviewRowViewModel(
        "calculationReview.lta",
        None,
        "N/A",
        None
      )
    )

  def outDatesSummary(calculationResponse: CalculationResponse): Seq[IndividualAASummaryModel] =
    calculationResponse.outDates.map { outDate =>
      val changeInTaxChargeAmount =
        outDate.adjustedCompensation.getOrElse(0)

      val messageKey = if (changeInTaxChargeAmount > 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.decrease."
      } else {
        "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
      }

      IndividualAASummaryModel(
        outDate.period,
        changeInTaxChargeAmount.abs,
        changeInTaxChargeAmount,
        messageKey,
        outDate.revisedChargableAmountBeforeTaxRate,
        outDate.chargePaidByMember,
        outDate.chargePaidBySchemes,
        outDate.revisedChargableAmountAfterTaxRate,
        outDate.chargePaidByMember + outDate.chargePaidBySchemes,
        Some(outDate.revisedChargableAmountAfterTaxRate - (outDate.chargePaidByMember + outDate.chargePaidBySchemes))
      )
    }

  def calculationReviewViewModel(calculationResponse: CalculationResponse): CalculationReviewViewModel = {
    val outDatesVal: Seq[Seq[ReviewRowViewModel]]     = outDatesReview(calculationResponse)
    val inDatesVal: Seq[Seq[ReviewRowViewModel]]      = inDatesReview(calculationResponse)
    val lifetimeAllowanceVal: Seq[ReviewRowViewModel] = lifetimeAllowanceReview
    val resubmissionVal: Resubmission                 = calculationResponse.resubmission
    CalculationReviewViewModel(outDatesVal, inDatesVal, lifetimeAllowanceVal, resubmissionVal)
  }

  private def outDatesReview(calculationResponse: CalculationResponse): Seq[Seq[ReviewRowViewModel]] =
    calculationResponse.outDates.map { outDate =>
      Seq(
        ReviewRowViewModel(
          "calculationReview.period." + outDate.period.toString,
          Some(changeInAAOutDateTaxCharge(outDate)),
          "controllers.routes.CalculationReviewIndividualAAController.onPageLoad(outDate.period).url",
          outDate.adjustedCompensation.map(Math.abs).orElse(Some(0))
        )
      )
    }

  private def changeInAAOutDateTaxCharge(outDate: OutOfDatesTaxYearsCalculation): String = {
    val totalCharge = outDate.adjustedCompensation
    if (totalCharge.contains(0)) {
      "calculationReview.taxChargeNotChanged"
    } else {
      "calculationReview.taxChargeDecreasedBy"
    }
  }

  private def inDatesReview(calculationResponse: CalculationResponse): Seq[Seq[ReviewRowViewModel]] =
    calculationResponse.inDates.map { inDate =>
      Seq(
        ReviewRowViewModel(
          "calculationReview.period." + inDate.period.toString,
          Some(changeInAAInDateTaxCharge(inDate)),
          "controllers.routes.CalculationReviewIndividualAAController.onPageLoad(inDate.period).url",
          inDate.totalCompensation.map(Math.abs).orElse(Some(0))
        )
      )
    }

  private def changeInAAInDateTaxCharge(inDate: InDatesTaxYearsCalculation): String = {
    val totalCharge = inDate.totalCompensation
    if (totalCharge.contains(0)) {
      "calculationReview.taxChargeNotChanged"
    } else if (totalCharge.exists(_ > 0)) {
      "calculationReview.taxChargeDecreasedBy"
    } else {
      "calculationReview.taxChargeIncreasedBy"
    }
  }

  def inDatesSummary(calculationResponse: CalculationResponse): Seq[IndividualAASummaryModel] =
    calculationResponse.inDates.map { inDate =>
      val changeInTaxChargeAmount = inDate.totalCompensation.getOrElse(0)

      val messageKey = if (changeInTaxChargeAmount > 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.decrease."
      } else if (changeInTaxChargeAmount < 0) {
        "calculationReviewIndividualAA.changeInTaxChargeString.increase."
      } else {
        "calculationReviewIndividualAA.changeInTaxChargeString.noChange."
      }

      IndividualAASummaryModel(
        inDate.period,
        changeInTaxChargeAmount.abs,
        changeInTaxChargeAmount,
        messageKey,
        inDate.revisedChargableAmountBeforeTaxRate,
        inDate.chargePaidByMember,
        inDate.chargePaidBySchemes,
        inDate.revisedChargableAmountAfterTaxRate,
        inDate.chargePaidByMember + inDate.chargePaidBySchemes,
        None
      )
    }

  def calculationReviewIndividualAAViewModel(
    calculateResponse: CalculationResponse,
    period: Option[String],
    calculationInputs: CalculationInputs
  )(implicit ec: ExecutionContext, hc: HeaderCarrier): CalculationReviewIndividualAAViewModel = {
    val outDates: Seq[Seq[RowViewModel]] = outDatesReviewAA(calculateResponse, period, calculationInputs)
    val inDates: Seq[Seq[RowViewModel]]  = inDatesReviewAA(calculateResponse, period, calculationInputs)

    CalculationReviewIndividualAAViewModel(outDates, inDates)
  }

  def outDatesReviewAA(
    calculateResponse: CalculationResponse,
    period: Option[String],
    calculationInputs: CalculationInputs
  ): Seq[Seq[RowViewModel]] = {

    val taxYears: List[TaxYear2016To2023] =
      calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect { case ty: TaxYear2016To2023 =>
        ty
      }

    outDatesReviewAAFiltered(period, calculateResponse.outDates)
      .map { outDate =>
        val taxYear: IncomeSubJourney = taxYearIncomeSubJourney(taxYears, outDate.period)
        Seq(
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.chargePaidByMember",
            currencyFormat(outDate.chargePaidByMember.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.chargePaidBySchemes",
            currencyFormat(outDate.chargePaidBySchemes.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.reducedNetIncome",
            currencyFormat(taxYear.reducedNetIncomeAmount.getOrElse(0).toString)
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.personalAllowance",
            currencyFormat(taxYear.personalAllowanceAmount.getOrElse(0).toString)
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.thresholdIncome",
            currencyFormat(thresholdIncomeMessage(outDate.period, taxYear))
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.adjustedIncome",
            currencyFormat(adjustedIncomeMessage(outDate.period, taxYear))
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.unusedAnnualAllowance",
            currencyFormat(outDate.unusedAnnualAllowance.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountBeforeTaxRate",
            currencyFormat(outDate.revisedChargableAmountBeforeTaxRate.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.revisedChargeableAmountAfterTaxRate",
            currencyFormat(outDate.revisedChargableAmountAfterTaxRate.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.amountYouOwe",
            currencyFormat("0")
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.directCompensation",
            currencyFormat(outDate.directCompensation.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.outDates.indirectCompensation",
            currencyFormat(outDate.indirectCompensation.toString())
          )
        )
      }
  }

  private def outDatesReviewAAFiltered(
    period: Option[String],
    outDates: List[OutOfDatesTaxYearsCalculation]
  ): List[OutOfDatesTaxYearsCalculation] =
    if (period.isDefined) {
      outDates.filter(outDate => outDate.period.toString == period.get)
    } else outDates

  private def inDatesReviewAA(
    calculateResponse: CalculationResponse,
    period: Option[String],
    calculationInputs: CalculationInputs
  ): Seq[Seq[RowViewModel]] = {
    val taxYears: List[TaxYear2016To2023] =
      calculationInputs.annualAllowance.map(_.taxYears).getOrElse(List()).collect { case ty: TaxYear2016To2023 =>
        ty
      }

    inDatesReviewAAFiltered(period, calculateResponse.inDates)
      .map { inDate =>
        val taxYear: IncomeSubJourney = taxYearIncomeSubJourney(taxYears, inDate.period)
        Seq(
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.chargePaidByMember",
            currencyFormat(inDate.chargePaidByMember.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.chargePaidBySchemes",
            currencyFormat(inDate.chargePaidBySchemes.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.reducedNetIncome",
            currencyFormat(taxYear.reducedNetIncomeAmount.getOrElse(0).toString)
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.personalAllowance",
            currencyFormat(taxYear.personalAllowanceAmount.getOrElse(0).toString)
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.thresholdIncome",
            currencyFormat(thresholdIncomeMessage(inDate.period, taxYear))
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.adjustedIncome",
            currencyFormat(adjustedIncomeMessage(inDate.period, taxYear))
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.unusedAnnualAllowance",
            currencyFormat(inDate.unusedAnnualAllowance.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountBeforeTaxRate",
            currencyFormat(inDate.revisedChargableAmountBeforeTaxRate.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.revisedChargeableAmountAfterTaxRate",
            currencyFormat(inDate.revisedChargableAmountAfterTaxRate.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.debit",
            currencyFormat(inDate.debit.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.memberCredit",
            currencyFormat(inDate.memberCredit.toString())
          ),
          RowViewModel(
            "calculationReviewIndividualAA.annualResults.inDates.schemeCredit",
            currencyFormat(inDate.schemeCredit.toString())
          )
        )
      }
  }

  private def inDatesReviewAAFiltered(
    period: Option[String],
    inDates: List[InDatesTaxYearsCalculation]
  ): List[InDatesTaxYearsCalculation] =
    if (period.isDefined) inDates.filter(inDate => inDate.period.toString == period.get)
    else inDates

  private def taxYearIncomeSubJourney(taxYears: List[TaxYear2016To2023], period: Period): IncomeSubJourney =
    taxYears.filter(ty => ty.period == period.toCalculationInputsPeriod).head match {
      case ty: NormalTaxYear                  => ty.incomeSubJourney
      case ty: InitialFlexiblyAccessedTaxYear => ty.incomeSubJourney
      case ty: PostFlexiblyAccessedTaxYear    => ty.incomeSubJourney
    }

  private def thresholdIncomeMessage(
    period: Period,
    incomeSubJourney: IncomeSubJourney
  ): String =
    period match {
      case Period._2016 => "site.notApplicable"
      case _            => incomeSubJourney.thresholdIncomeAmount.map(_.toString).getOrElse("notApplicable")
    }

  private def adjustedIncomeMessage(period: Period, incomeSubJourney: IncomeSubJourney): String =
    period match {
      case Period._2016 => "site.notApplicable"
      case _            => incomeSubJourney.adjustedIncomeAmount.map(_.toString).getOrElse("notApplicable")
    }

}
