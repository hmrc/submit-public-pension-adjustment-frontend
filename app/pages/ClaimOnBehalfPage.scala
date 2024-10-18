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

package pages

import controllers.routes
import models.submission.Submission
import models.{CheckMode, Mode, NormalMode, Period, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.{ClaimOnBehalfNavigationLogicService, PeriodService}

import scala.util.Try

case object ClaimOnBehalfPage extends QuestionPageWithLTAOnlyNavigation[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "claimOnBehalf"

  override def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(NormalMode)
      case Some(false) =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(CheckMode)
      case Some(false) =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, CheckMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(NormalMode)
      case Some(false) => ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(ClaimOnBehalfPage) match {
      case Some(true)  => routes.StatusOfUserController.onPageLoad(CheckMode)
      case Some(false) => ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(CheckMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val periodsToCleanup = PeriodService.allInDateRemedyPeriods
    value
      .map {
        case false =>
          onBehalfOfCleanup(userAnswers)

        case true =>
          Try(ClaimOnBehalfNavigationLogicService.periodPageCleanup(userAnswers, periodsToCleanup))
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
}
