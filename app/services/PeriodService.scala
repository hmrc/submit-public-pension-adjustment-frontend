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

import models.Period
import models.calculation.response.CalculationResponse
import models.submission.Submission

object PeriodService {

  def getFirstDebitPeriod(submission: Submission): Option[Period] = submission.calculation match {
    case Some(calculationResponse) =>
      val firstCalcPeriod: Option[Period] = orderedInDateDebitPeriods(calculationResponse).headOption
      firstCalcPeriod.map(p => Period.fromString(p.toString)).getOrElse(None)
    case None                      => None
  }

  def getNextDebitPeriod(submission: Submission, currentPeriod: Period): Option[Period] =
    submission.calculation match {
      case Some(calculationResponse) =>
        orderedInDateDebitPeriods(calculationResponse).find(p => p.start.isAfter(currentPeriod.start))
      case None                      => None
    }

  def orderedInDateDebitPeriods(calculationResponse: CalculationResponse): Seq[Period] =
    calculationResponse.inDates
      .filter(yearCalculation => yearCalculation.debit > 0)
      .map(debitYearCalculation => debitYearCalculation.period)
      .sorted
      .flatMap(p => Period.fromString(p.toString))

  def allInDateRemedyPeriods =
    Seq(
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    )

}
