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

package services

import models.calculation.inputs.{CalculationInputs, LifeTimeAllowance, TaxYear2016To2023}
import models.{PSTR, PensionSchemeDetails, WhichPensionSchemeWillPay, WhichPensionSchemeWillPayTaxRelief}

object SchemeService {

  def schemeName(pstr: PSTR, calculationInputs: CalculationInputs): String = allPensionSchemeDetails(calculationInputs)
    .find(psd => psd.pensionSchemeTaxReference == pstr.value)
    .map(identifiedScheme => identifiedScheme.pensionSchemeName)
    .getOrElse("")

  def allPensionSchemeDetails(calculationInputs: CalculationInputs): Seq[PensionSchemeDetails] =
    getAllPensionSchemeDetails(calculationInputs).map(taxYearScheme =>
      PensionSchemeDetails(taxYearScheme.pensionSchemeName, taxYearScheme.pensionSchemeTaxReference)
    )

  def allSchemeDetails(calculationInputs: CalculationInputs): WhichPensionSchemeWillPay = {
    val pensionSchemeDetails: Seq[String] =
      getAllPensionSchemeDetails(calculationInputs).map(taxYearScheme => schemeNameAndReference(taxYearScheme))
    WhichPensionSchemeWillPay(pensionSchemeDetails :+ PSTR.New)
  }

  def allSchemeDetailsForTaxRelief(calculationInputs: CalculationInputs): WhichPensionSchemeWillPayTaxRelief = {
    val pensionSchemeDetails: Seq[String] =
      getAllPensionSchemeDetails(calculationInputs).map(taxYearScheme => schemeNameAndReference(taxYearScheme))
    WhichPensionSchemeWillPayTaxRelief(pensionSchemeDetails)
  }

  def allSchemeDetailsForTaxReliefLength(calculationInputs: CalculationInputs): Int =
    getAllPensionSchemeDetails(calculationInputs).length

  private def getAllPensionSchemeDetails(calculationInputs: CalculationInputs): Seq[PensionSchemeDetails] =
    schemesFromAAInputs(calculationInputs) ++ schemesFromLtaInputs(calculationInputs)

  private def schemesFromAAInputs(calculationInputs: CalculationInputs) =
    calculationInputs.annualAllowance
      .map {
        _.taxYears flatMap {
          case TaxYear2016To2023.NormalTaxYear(_, taxYearSchemes, _, _, _, _)                           =>
            List(taxYearSchemes)
          case TaxYear2016To2023.InitialFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _, _, _) =>
            List(taxYearSchemes)
          case TaxYear2016To2023.PostFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _)          =>
            List(taxYearSchemes)
          case _                                                                                        => Nil
        }
      }
      .getOrElse(Nil)
      .flatten
      .distinctBy(_.pensionSchemeTaxReference)
      .map(t => PensionSchemeDetails(t.name, t.pensionSchemeTaxReference))

  private def schemesFromLtaInputs(calculationInputs: CalculationInputs) = {
    val maybeLta: Option[LifeTimeAllowance] = calculationInputs.lifeTimeAllowance

    Seq(
      maybeLta
        .flatMap(lta => lta.previousLifetimeAllowanceChargeSchemeNameAndTaxRef)
        .map(nameAndRef => PensionSchemeDetails(nameAndRef.name, nameAndRef.taxRef)),
      maybeLta
        .flatMap(lta => lta.newLifetimeAllowanceChargeSchemeNameAndTaxRef)
        .map(nameAndRef => PensionSchemeDetails(nameAndRef.name, nameAndRef.taxRef))
    ).flatten
  }

  private def schemeNameAndReference(taxYearScheme: PensionSchemeDetails) =
    s"${taxYearScheme.pensionSchemeName} / ${taxYearScheme.pensionSchemeTaxReference}"
}
