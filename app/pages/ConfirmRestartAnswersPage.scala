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

import models.UserAnswers
import play.api.libs.json.JsPath
import play.api.mvc.Call
import controllers.routes

case object ConfirmRestartAnswersPage extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "confirmRestartAnswers"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(ConfirmRestartAnswersPage) match {
      case Some(true)  => controllers.routes.RestartCalculationController.restartCalculation
      case Some(false) => controllers.routes.ContinueChoiceController.onPageLoad()
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
