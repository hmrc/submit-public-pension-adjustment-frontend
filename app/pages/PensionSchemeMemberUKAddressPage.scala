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

import models.{NormalMode, PensionSchemeMemberUKAddress, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes

case object PensionSchemeMemberUKAddressPage extends QuestionPage[PensionSchemeMemberUKAddress] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberUKAddress"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) => routes.AlternativeNameController.onPageLoad(NormalMode)
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(PensionSchemeMemberUKAddressPage) match {
      case Some(_) => routes.CheckYourAnswersController.onPageLoad
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }
}
