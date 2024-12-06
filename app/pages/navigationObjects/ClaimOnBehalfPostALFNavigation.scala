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

package pages.navigationObjects

import controllers.routes
import models.{CheckMode, Mode, NormalMode, StatusOfUser, UserAnswers}
import models.submission.Submission
import pages.StatusOfUserPage
import play.api.mvc.Call
import services.ClaimOnBehalfNavigationLogicService

object ClaimOnBehalfPostALFNavigation {

  def navigate(answers: UserAnswers, submission: Submission, mode: Mode): Call =
    if (mode == NormalMode) {
      navigateInNormalMode(answers, submission)
    } else {
      navigateInCheckMode(answers, submission)
    }
  def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    if (ltaOnly(submission)) {
      navigateInNormalModeLTAOnly(answers)
    } else {
      navigateInNormalModeAA(answers, submission)
    }

  def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    if (ltaOnly(submission)) {
      navigateInCheckModeLTAOnly(answers)
    } else {
      navigateInCheckModeAA(answers, submission)
    }

  def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(StatusOfUserPage) match {
      case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.AlternativeNameController.onPageLoad(NormalMode)
      case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, NormalMode)
      case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
    }

  def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(StatusOfUserPage) match {
      case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.CheckYourAnswersController.onPageLoad
      case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, CheckMode)
      case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
    }

  def navigateInNormalModeLTAOnly(answers: UserAnswers): Call =
    answers.get(StatusOfUserPage) match {
      case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.AlternativeNameController.onPageLoad(NormalMode)
      case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(NormalMode)
      case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
    }

  def navigateInCheckModeLTAOnly(answers: UserAnswers): Call =
    answers.get(StatusOfUserPage) match {
      case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.CheckYourAnswersController.onPageLoad
      case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
        ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(CheckMode)
      case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
    }

  def ltaOnly(submission: Submission): Boolean = {
    val calculationInputs = submission.calculationInputs
    calculationInputs.lifeTimeAllowance.isDefined && calculationInputs.annualAllowance.forall(_.taxYears.isEmpty)
  }
}
