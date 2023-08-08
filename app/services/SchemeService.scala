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

import models.{PSTR, PensionSchemeDetails, Period, UserAnswers, WhichPensionSchemeWillPay}
import models.calculation.inputs.CalculationInputs
import models.calculation.inputs.TaxYear2016To2023

object SchemeService {

//  def whichPensionSchemeWillPay(answers: UserAnswers): WhichPensionSchemeWillPay = {
//    val schemeRefs: Seq[PSTR]        = allSchemeRefs(answers)
//    val schemeRefsOrNew: Seq[String] = schemeRefs.map(pstr => pstr.value)
//    WhichPensionSchemeWillPay(schemeRefsOrNew :+ PSTR.New)

//  def maybeAddSchemeDetailsToPeriod(
//    answers: UserAnswers,
//    schemeRef: String,
//    period: Period,
//    schemeIndex: SchemeIndex
//  ): UserAnswers =
//    if (schemeRef != PSTR.New) {
//      val schemeName: Option[String] = findSchemeName(answers, schemeRef)
//
//      schemeName match {
//        case Some(schemeName) =>
//          answers.set(PensionSchemeDetailsPage(period, schemeIndex), PensionSchemeDetails(schemeName, schemeRef)).get
//        case None             => answers
//      }
//    } else {
//      answers
//    }

//  private def allSchemeRefs(answers: UserAnswers): Seq[PSTR] =
//    allSchemeDetails(answers).map(detail => PSTR(detail.schemeTaxRef)).distinct

//  private def findSchemeName(answers: UserAnswers, schemeRef: String): Option[String] = {
//    val allDetails: Seq[PensionSchemeDetails] = allSchemeDetails(answers)
//    allDetails.find(details => details.schemeTaxRef == schemeRef).map(psd => psd.schemeName)
//  }

//  private def allSchemeDetails(answers: UserAnswers): Seq[PensionSchemeDetails] = {
//    val allSchemeDetailsOptions: Seq[Option[PensionSchemeDetails]] = for {
//      period <- PeriodService.allRemedyPeriods
//      index  <- allSchemeIndices
//    } yield answers.get(PensionSchemeDetailsPage(period, index))
//    val allSchemeDetails: Seq[PensionSchemeDetails]                = allSchemeDetailsOptions.flatten
//    allSchemeDetails
//  }
//
//  private def allSchemeIndices: Seq[SchemeIndex] = 0.to(4).map(i => SchemeIndex(i))

  def allSchemeDetails(calculationInputs: CalculationInputs): WhichPensionSchemeWillPay = {
    val pensionSchemeDetails: List[(String, String)] = calculationInputs.annualAllowance
      .map {
        _.taxYears flatMap {
          case TaxYear2016To2023.NormalTaxYear(_, taxYearSchemes, _, _, _, _) =>
            List(taxYearSchemes.map(ps => (ps.name, ps.pensionSchemeTaxReference)))
          case TaxYear2016To2023.InitialFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _, _, _) =>
            List(taxYearSchemes.map(ps => (ps.name, ps.pensionSchemeTaxReference)))
          case TaxYear2016To2023.PostFlexiblyAccessedTaxYear(_, _, _, _, taxYearSchemes, _, _) =>
            List(taxYearSchemes.map(ps => (ps.name, ps.pensionSchemeTaxReference)))
        }
      }
      .getOrElse(Nil)
      .flatten

    WhichPensionSchemeWillPay(pensionSchemeDetails)

  }

}
