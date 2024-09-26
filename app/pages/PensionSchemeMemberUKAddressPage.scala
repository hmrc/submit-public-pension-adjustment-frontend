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

import models.{CheckMode, NormalMode, PensionSchemeMemberUKAddress, StatusOfUser, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes
import models.submission.Submission
import services.{ClaimOnBehalfNavigationLogicService, PeriodService}

import scala.util.Try

case object PensionSchemeMemberUKAddressPage extends QuestionPageWithLTAOnlyNavigation[PensionSchemeMemberUKAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberUKAddress"

  override def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) =>
        answers.get(StatusOfUserPage) match {
          case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.AlternativeNameController.onPageLoad(NormalMode)
          case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
            ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, NormalMode)
          case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) =>
        answers.get(StatusOfUserPage) match {
          case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.CheckYourAnswersController.onPageLoad
          case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
            ClaimOnBehalfNavigationLogicService.handleNavigateInAA(submission, answers, CheckMode)
          case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) =>
        answers.get(StatusOfUserPage) match {
          case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.AlternativeNameController.onPageLoad(NormalMode)
          case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
            ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(NormalMode)
          case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) =>
        answers.get(StatusOfUserPage) match {
          case Some(StatusOfUser.LegalPersonalRepresentative)                     => routes.CheckYourAnswersController.onPageLoad
          case Some(status) if status != StatusOfUser.LegalPersonalRepresentative =>
            ClaimOnBehalfNavigationLogicService.handleNavigateInLTA(CheckMode)
          case _                                                                  => routes.JourneyRecoveryController.onPageLoad(None)
        }
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[PensionSchemeMemberUKAddress], userAnswers: UserAnswers): Try[UserAnswers] = {
    val periodsToCleanup = PeriodService.allInDateRemedyPeriods
    value
      .map { case _ =>
        userAnswers.get(StatusOfUserPage) match {
          case Some(StatusOfUser.LegalPersonalRepresentative) =>
            Try(ClaimOnBehalfNavigationLogicService.periodPageCleanup(userAnswers, periodsToCleanup))
          case _                                              => super.cleanup(value, userAnswers)
        }
      }
      .getOrElse(super.cleanup(value, userAnswers))
  }
}
