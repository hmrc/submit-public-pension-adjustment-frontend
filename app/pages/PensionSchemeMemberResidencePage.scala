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

import models.{CheckMode, NormalMode, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes

import scala.util.Try

case object PensionSchemeMemberResidencePage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberResidence"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PensionSchemeMemberResidencePage) match {
      case Some(true)  => routes.PensionSchemeMemberUKAddressController.onPageLoad(NormalMode)
      case Some(false) => routes.PensionSchemeMemberInternationalAddressController.onPageLoad(NormalMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PensionSchemeMemberResidencePage) match {
      case Some(true)  => routes.PensionSchemeMemberUKAddressController.onPageLoad(CheckMode)
      case Some(false) => routes.PensionSchemeMemberInternationalAddressController.onPageLoad(CheckMode)
      case None        => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => userAnswers.remove(PensionSchemeMemberUKAddressPage)
        case true  => userAnswers.remove(PensionSchemeMemberInternationalAddressPage)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
