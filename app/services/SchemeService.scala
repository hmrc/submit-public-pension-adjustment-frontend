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

import models.{PSTR, WhichPensionSchemeWillPay, WhichPensionSchemeWillPayTaxRelief}
import models.calculation.inputs.CalculationInputs
import models.calculation.inputs.TaxYear2016To2023
import models.calculation.response.TaxYearScheme

object SchemeService {

  def allSchemeDetails(calculationInputs: CalculationInputs): WhichPensionSchemeWillPay = {
    val pensionSchemeDetails: Seq[String] = getAllPensionSchemeDetails(calculationInputs)
    WhichPensionSchemeWillPay(pensionSchemeDetails :+ PSTR.New)
  }

  def allSchemeDetailsForTaxRelief(calculationInputs: CalculationInputs): WhichPensionSchemeWillPayTaxRelief = {
    val pensionSchemeDetails: Seq[String] = getAllPensionSchemeDetails(calculationInputs)
    WhichPensionSchemeWillPayTaxRelief(pensionSchemeDetails)
  }

  def allSchemeDetailsForTaxReliefLength(calculationInputs: CalculationInputs): Int =
    getAllPensionSchemeDetails(calculationInputs).length

  private def getAllPensionSchemeDetails(calculationInputs: CalculationInputs) =
    calculationInputs.annualAllowance
      .map {
        _.taxYears flatMap {
          case TaxYear2016To2023.NormalTaxYear(_, taxYearSchemes, _, _, _, _)                           =>
            List(taxYearSchemes.map(psd => schemeNameAndReference(psd)))
          case TaxYear2016To2023.InitialFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _, _, _) =>
            List(taxYearSchemes.map(psd => schemeNameAndReference(psd)))
          case TaxYear2016To2023.PostFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _)          =>
            List(taxYearSchemes.map(psd => schemeNameAndReference(psd)))
          case _                                                                                        => Nil
        }
      }
      .getOrElse(Nil)
      .flatten
      .distinct

  private def schemeNameAndReference(pensionSchemeDetails: TaxYearScheme) =
    s"${pensionSchemeDetails.name} / ${pensionSchemeDetails.pensionSchemeTaxReference}"
}
