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

package services

import controllers.routes
import models.submission.Submission
import models.{CheckMode, Mode, NormalMode, Period, UserAnswers}
import pages._
import play.api.mvc.Call

object ClaimOnBehalfNavigationLogicService {

  def handleNavigateInAA(
    submission: Submission,
    answers: UserAnswers,
    mode: Mode
  ): Call =
    submission.calculation match {
      case Some(calculation) =>
        if (calculation.totalAmounts.inDatesDebit > 0) {
          navigateWhenTotalAmountsHasInDateDebit(submission, mode, answers)
        } else {
          if (mode == CheckMode) {
            routes.CheckYourAnswersController.onPageLoad()
          } else {
            routes.AlternativeNameController.onPageLoad(mode)
          }
        }
      case None              => routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateWhenTotalAmountsHasInDateDebit(submission: Submission, mode: Mode, answers: UserAnswers) = {
    val maybePeriod = PeriodService.getFirstDebitPeriod(submission)
    maybePeriod match {
      case Some(period) =>
        answers.get(WhoWillPayPage(period)) match {
          case Some(_) if mode == CheckMode =>
            routes.CheckYourAnswersController.onPageLoad()
          case _                            => routes.WhoWillPayController.onPageLoad(NormalMode, period)
        }
      case None         => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  def handleNavigateInLTA(
    mode: Mode
  ): Call =
    if (mode == CheckMode) {
      routes.CheckYourAnswersController.onPageLoad()
    } else {
      routes.AlternativeNameController.onPageLoad(mode)
    }

  def periodPageCleanup(answers: UserAnswers, periods: Seq[Period]): UserAnswers =
    periods.headOption match {
      case Some(period) =>
        periodPageCleanup(
          answers
            .remove(WhoWillPayPage(period))
            .flatMap(_.remove(WhichPensionSchemeWillPayPage(period)))
            .flatMap(_.remove(PensionSchemeDetailsPage(period)))
            .flatMap(_.remove(AskedPensionSchemeToPayTaxChargePage(period)))
            .flatMap(_.remove(WhenWillYouAskPensionSchemeToPayPage(period)))
            .flatMap(_.remove(WhenDidYouAskPensionSchemeToPayPage(period)))
            .flatMap(_.remove(SchemeElectionConsentPage(period)))
            .get,
          periods.tail
        )
      case None         => answers
    }
}
