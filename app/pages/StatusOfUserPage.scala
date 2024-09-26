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

import models.{CheckMode, NormalMode, StatusOfUser, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes
import models.StatusOfUser.{Deputyship, LegalPersonalRepresentative, PowerOfAttorney}

import scala.util.Try

case object StatusOfUserPage extends QuestionPage[StatusOfUser] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "statusOfUser"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(StatusOfUserPage) match {
      case Some(_) => routes.PensionSchemeMemberNameController.onPageLoad(NormalMode)
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(StatusOfUserPage) match {
      case Some(_) => routes.PensionSchemeMemberNameController.onPageLoad(CheckMode)
      case None    => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[StatusOfUser], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case PowerOfAttorney =>
          userAnswers.remove(MemberDateOfDeathPage)

        case LegalPersonalRepresentative =>
          super.cleanup(value, userAnswers)

        case Deputyship =>
          super.cleanup(value, userAnswers)
      }
      .getOrElse(super.cleanup(value, userAnswers))

}
