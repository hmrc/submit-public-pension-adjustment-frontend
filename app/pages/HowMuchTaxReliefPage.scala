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

import models.submission.Submission
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.SchemeService

import scala.util.Try

case object HowMuchTaxReliefPage extends QuestionPageWithLTAOnlyNavigation[BigInt] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "howMuchTaxRelief"

  override def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => isSchemePageValid(submission, NormalMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call =
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) => isSchemePageValid(answers, submission, CheckMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    answers.get(HowMuchTaxReliefPage) match {
      case Some(_) =>
        val numberOfSchemes: Int = SchemeService.allSchemeDetailsForTaxReliefLength(submission.calculationInputs)
        numberOfSchemes match {
          case 0 => controllers.routes.JourneyRecoveryController.onPageLoad(None)
          case 1 => controllers.routes.CheckYourAnswersController.onPageLoad()
          case _ => controllers.routes.WhichPensionSchemeWillPayTaxReliefController.onPageLoad(NormalMode)
        }
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    navigateInNormalModeLTAOnly(answers, submission)

  private def isSchemePageValid(submission: Submission, mode: Mode): Call = {
    val numberOfSchemes: Int = SchemeService.allSchemeDetailsForTaxReliefLength(submission.calculationInputs)
    val memberCredit         = submission.calculation.map(_.inDates.map(_.memberCredit).sum).getOrElse(0)

    if (memberCredit > 0) {
      whenMemberIsInCredit(mode, numberOfSchemes)
    } else {
      whenMemberIsNotInCredit(mode, numberOfSchemes)
    }
  }

  private def whenMemberIsNotInCredit(mode: Mode, numberOfSchemes: Int) =
    if (numberOfSchemes == 1) {
      controllers.routes.CheckYourAnswersController.onPageLoad()
    } else {
      controllers.routes.WhichPensionSchemeWillPayTaxReliefController.onPageLoad(mode)
    }

  private def whenMemberIsInCredit(mode: Mode, numberOfSchemes: Int) =
    if (numberOfSchemes == 1) {
      controllers.routes.BavfRampOnController.rampOnBavf(mode)
    } else {
      controllers.routes.WhichPensionSchemeWillPayTaxReliefController.onPageLoad(mode)
    }

  override def cleanup(value: Option[BigInt], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { case _ =>
        userAnswers
          .remove(WhichPensionSchemeWillPayTaxReliefPage)
          .flatMap(_.remove(BankDetailsPage))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
