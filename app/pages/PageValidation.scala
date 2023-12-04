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

package pages

import models.submission.Submission
import models.{Period, UserAnswers, WhoWillPay}
import play.api.Logging
import services.PeriodService

object PageValidation extends Logging {

  def incompleteRequiredPages(submission: Submission, answers: UserAnswers): Seq[Page] = {
    val requiredPages = allPossiblePages(submission).filter(page => page.isRequired(answers).contains(true))
    logger.info(s"requiredPages : $requiredPages")
    requiredPages.filterNot(page => answers.containsAnswerFor(page))
  }

  private def allPossiblePages(submission: Submission): Seq[QuestionPage[_]] = {
    val inDateDebitPeriods: Seq[Period] = submission.calculation
      .map(calculationResponse => PeriodService.orderedInDateDebitPeriods(calculationResponse))
      .getOrElse(Seq.empty[Period])
    val debitPeriodPages                = inDateDebitPeriodPages(inDateDebitPeriods)
    val claimOnBehalfPages              = claimOnBehalfOfPages()

    debitPeriodPages ++ claimOnBehalfPages
  }

  private def inDateDebitPeriodPages(inDateDebitPeriods: Seq[Period]): Seq[QuestionPage[_]] =
    inDateDebitPeriods.map(period => inDateDebitPeriodPages(period)).flatten

  def inDateDebitPeriodPages(period: Period): Seq[QuestionPage[_]] =
    Seq(
      WhoWillPayPage(period),
      WhichPensionSchemeWillPayPage(period),
      PensionSchemeDetailsPage(period),
      AskedPensionSchemeToPayTaxChargePage(period),
      WhenDidYouAskPensionSchemeToPayPage(period),
      WhenWillYouAskPensionSchemeToPayPage(period)
    )

  def claimOnBehalfOfPages() = Seq(
    StatusOfUserPage,
    PensionSchemeMemberNamePage,
    PensionSchemeMemberNinoPage,
    PensionSchemeMemberDOBPage,
    MemberDateOfDeathPage,
    PensionSchemeMemberResidencePage,
    PensionSchemeMemberInternationalAddressPage,
    PensionSchemeMemberUKAddressPage
  )

  def schemeWillPay(period: Period, answers: UserAnswers): Option[Boolean] =
    answers.get(WhoWillPayPage(period)) match {
      case Some(WhoWillPay.You)           => Some(false)
      case Some(WhoWillPay.PensionScheme) => Some(true)
      case _                              => None
    }

  def claimingOnBehalf(answers: UserAnswers): Option[Boolean] = answers.get(ClaimOnBehalfPage)
}
