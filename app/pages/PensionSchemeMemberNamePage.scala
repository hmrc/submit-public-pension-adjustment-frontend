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
import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

case object PensionSchemeMemberNamePage extends QuestionPage[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberName"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    controllers.routes.PensionSchemeMemberDOBController.onPageLoad(NormalMode)

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RunThroughOnBehalfFlow()) match {
      case Some(true)     => controllers.routes.PensionSchemeMemberDOBController.onPageLoad(CheckMode)
      case Some(_) | None => routes.CheckYourAnswersController.onPageLoad()
    }
}
