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
import uk.gov.hmrc.domain.Nino

case object PensionSchemeMemberNinoPage extends QuestionPage[Nino] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "pensionSchemeMemberNino"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(PensionSchemeMemberNinoPage) match {
      case Some(_) => routes.PensionSchemeMemberTaxReferenceController.onPageLoad(NormalMode)
      case _       => routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(RunThroughOnBehalfFlow()) match {
      case Some(true)     => controllers.routes.PensionSchemeMemberTaxReferenceController.onPageLoad(CheckMode)
      case Some(_) | None => routes.CheckYourAnswersController.onPageLoad
    }
}
