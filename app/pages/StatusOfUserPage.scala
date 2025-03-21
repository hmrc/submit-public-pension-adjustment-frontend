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
import models.StatusOfUser.{Deputyship, LegalPersonalRepresentative, PowerOfAttorney}
import models.submission.Submission
import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow, StatusOfUser, UserAnswers}
import pages.navigationObjects.ClaimOnBehalfPostALFNavigation
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.{ClaimOnBehalfNavigationLogicService, PeriodService}

import scala.util.Try

case object StatusOfUserPage extends QuestionPage[StatusOfUser] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "statusOfUser"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(StatusOfUserPage) match {
      case Some(_) => routes.PensionSchemeMemberNameController.onPageLoad(NormalMode)
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    if (answers.get(RunThroughOnBehalfFlow()).getOrElse(false)) {
      routes.PensionSchemeMemberNameController.onPageLoad(CheckMode)
    } else {
      noClaimOnBehalfRunThroughNavLogic(answers, submission)
    }

  private def noClaimOnBehalfRunThroughNavLogic(answers: UserAnswers, submission: Submission): Call =
    answers.get(StatusOfUserPage) match {
      case Some(LegalPersonalRepresentative) if answers.get(MemberDateOfDeathPage).isDefined =>
        ClaimOnBehalfPostALFNavigation.navigate(answers, submission, CheckMode)
      case Some(LegalPersonalRepresentative)                                                 =>
        routes.MemberDateOfDeathController.onPageLoad(CheckMode)
      case Some(_)                                                                           => ClaimOnBehalfPostALFNavigation.navigate(answers, submission, CheckMode)
      case _                                                                                 => routes.JourneyRecoveryController.onPageLoad()
    }

  override def cleanup(value: Option[StatusOfUser], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case PowerOfAttorney =>
          userAnswers.remove(MemberDateOfDeathPage)

        case Deputyship =>
          userAnswers.remove(MemberDateOfDeathPage)

        case LegalPersonalRepresentative =>
          Try(ClaimOnBehalfNavigationLogicService.periodPageCleanup(userAnswers, PeriodService.allInDateRemedyPeriods))
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
