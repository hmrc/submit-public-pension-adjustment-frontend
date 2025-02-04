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
import models.StatusOfUser.LegalPersonalRepresentative
import models.submission.Submission
import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow, UserAnswers}
import pages.navigationObjects.ClaimOnBehalfPostALFNavigation
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate

case object MemberDateOfDeathPage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "memberDateOfDeath"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(MemberDateOfDeathPage) match {
      case Some(_) => controllers.routes.PensionSchemeMemberNinoController.onPageLoad(NormalMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad()
    }

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    if (answers.get(RunThroughOnBehalfFlow()).getOrElse(false)) {
      controllers.routes.PensionSchemeMemberNinoController.onPageLoad(CheckMode)
    } else {
      answers.get(StatusOfUserPage) match {
        case Some(LegalPersonalRepresentative) =>
          routes.CheckYourAnswersController.onPageLoad()
        case Some(_)                           =>
          ClaimOnBehalfPostALFNavigation.navigate(answers, submission, CheckMode)
        case _                                 => routes.JourneyRecoveryController.onPageLoad()
      }
    }
}
