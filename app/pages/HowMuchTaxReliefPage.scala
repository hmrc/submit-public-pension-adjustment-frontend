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

import models.submission.Submission
import models.{Mode, NormalMode, UserAnswers, WhichPensionSchemeWillPayTaxRelief}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.SchemeService

case object HowMuchTaxReliefPage extends QuestionPage[BigInt] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "howMuchTaxRelief"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => isSchemePageValid(answers, submission, NormalMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  private def isSchemePageValid(answers: UserAnswers, submission: Submission, mode: Mode): Call = {
    val schemeDetails: WhichPensionSchemeWillPayTaxRelief = SchemeService.allSchemeDetailsForTaxRelief(submission.calculationInputs)
    val numberOfSchemes: Int = SchemeService.allSchemeDetailsForTaxReliefLength(submission.calculationInputs)
    val memberCredit = submission.calculation.map(_.inDates.map(_.memberCredit).sum).getOrElse(0)

    if (memberCredit > 0) {
      if (numberOfSchemes == 1) {
        answers.set(WhichPensionSchemeWillPayTaxReliefPage, schemeDetails.values.head)
        controllers.routes.BankDetailsController.onPageLoad(mode)
      } else {
        controllers.routes.WhichPensionSchemeWillPayTaxReliefController.onPageLoad(mode)
      }
    } else {
      if (numberOfSchemes == 1) {
        answers.set(WhichPensionSchemeWillPayTaxReliefPage, schemeDetails.values.head)
      }
      controllers.routes.DeclarationsController.onPageLoad
    }

  }
}
