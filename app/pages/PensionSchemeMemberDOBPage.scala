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
import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow, UserAnswers}

import java.time.LocalDate
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object PensionSchemeMemberDOBPage extends QuestionPage[LocalDate] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberDOB"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(StatusOfUserPage) match {
      case Some(status) if status == Deputyship || status == LegalPersonalRepresentative =>
        controllers.routes.MemberDateOfDeathController.onPageLoad(NormalMode)
      case Some(PowerOfAttorney)                                                         => controllers.routes.PensionSchemeMemberNinoController.onPageLoad(NormalMode)
      case _                                                                             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RunThroughOnBehalfFlow()) match {
      case Some(true)     =>
        answers.get(StatusOfUserPage) match {
          case Some(status) if status == Deputyship || status == LegalPersonalRepresentative =>
            controllers.routes.MemberDateOfDeathController.onPageLoad(CheckMode)
          case Some(PowerOfAttorney)                                                         => controllers.routes.PensionSchemeMemberNinoController.onPageLoad(CheckMode)
          case _                                                                             => controllers.routes.JourneyRecoveryController.onPageLoad(None)
        }
      case Some(_) | None => routes.CheckYourAnswersController.onPageLoad
    }
}
