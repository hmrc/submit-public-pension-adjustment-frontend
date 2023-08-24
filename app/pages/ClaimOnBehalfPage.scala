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

import controllers.routes
import models.submission.Submission
import models.{CheckMode, Mode, NormalMode, Period, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.PeriodService

import scala.util.Try

case object ClaimOnBehalfPage extends QuestionPageWithLTAOnlyNavigation[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimOnBehalf"

  override def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(NormalMode)
      case Some(false) =>
        submission.calculation match {
          case Some(calculation) =>
            if (calculation.totalAmounts.inDatesDebit > 0) {
              navigateWhenTotalAmountsHasInDateDebit(submission, NormalMode)
            } else {
              routes.AlternativeNameController.onPageLoad(NormalMode)
            }
          case None              => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def navigateWhenTotalAmountsHasInDateDebit(submission: Submission, mode: Mode) = {
    val maybePeriod = PeriodService.getFirstDebitPeriod(submission)
    maybePeriod match {
      case Some(period) => routes.WhoWillPayController.onPageLoad(mode, period)
      case None         => routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(NormalMode)
      case Some(false) =>
        submission.calculation match {
          case Some(calculation) =>
            if (calculation.totalAmounts.inDatesDebit > 0) {
              navigateWhenTotalAmountsHasInDateDebit(submission, CheckMode)
            } else {
              routes.CheckYourAnswersController.onPageLoad
            }
          case None              => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(NormalMode)
      case Some(false) => routes.AlternativeNameController.onPageLoad(NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(CheckMode)
      case Some(false) => routes.CheckYourAnswersController.onPageLoad
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val periodsToCleanup = PeriodService.allInDateRemedyPeriods
    value
      .map {
        case false =>
          onBehalfOfCleanup(userAnswers)

        case true =>
          Try(periodPageCleanup(userAnswers, periodsToCleanup))
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }

  private def onBehalfOfCleanup(userAnswers: UserAnswers) =
    userAnswers
      .remove(StatusOfUserPage)
      .flatMap(_.remove(PensionSchemeMemberNamePage))
      .flatMap(_.remove(PensionSchemeMemberDOBPage))
      .flatMap(_.remove(PensionSchemeMemberNinoPage))
      .flatMap(_.remove(MemberDateOfDeathPage))
      .flatMap(_.remove(PensionSchemeMemberTaxReferencePage))
      .flatMap(_.remove(PensionSchemeMemberResidencePage))
      .flatMap(_.remove(PensionSchemeMemberUKAddressPage))
      .flatMap(_.remove(PensionSchemeMemberInternationalAddressPage))

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
            .get,
          periods.tail
        )
      case None         => answers
    }

}
