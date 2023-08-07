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
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.PeriodService

import scala.util.Try

case object ClaimOnBehalfPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimOnBehalf"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
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

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(CheckMode)
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

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false =>
          for {
            updated <- userAnswers.remove(StatusOfUserPage)
          } yield updated
        case true  => super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
