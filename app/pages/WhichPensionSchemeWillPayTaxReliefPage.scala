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

import scala.util.Try

case object WhichPensionSchemeWillPayTaxReliefPage extends QuestionPageWithLTAOnlyNavigation[String] {

  override def path: JsPath = JsPath \ toString

  override def toString: String = "whichPensionSchemeWillPayTaxRelief"

  override def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call = {
    val selectedScheme: Option[String] = answers.get(WhichPensionSchemeWillPayTaxReliefPage)
    selectedScheme match {
      case Some(_) => isMemberCredit(submission, NormalMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call       = {
    val selectedScheme: Option[String] = answers.get(WhichPensionSchemeWillPayTaxReliefPage)
    selectedScheme match {
      case Some(_) => isMemberCredit(submission, CheckMode)
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }
  override def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    controllers.routes.CheckYourAnswersController.onPageLoad()

  override def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call =
    navigateInNormalModeLTAOnly(answers, submission)

  private def isMemberCredit(submission: Submission, mode: Mode): Call = {
    val memberCredit = submission.calculation.map(_.inDates.map(_.memberCredit).sum).getOrElse(0)
    if (memberCredit > 0) {
      controllers.routes.BavfRampOnController.rampOnBavf(mode)
    } else {
      controllers.routes.CheckYourAnswersController.onPageLoad()
    }
  }

  override def cleanup(value: Option[String], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map { case _ =>
        userAnswers.remove(BankDetailsPage)
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
