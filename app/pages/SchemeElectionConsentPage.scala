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
import models.{NormalMode, Period, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.PeriodService

case class SchemeElectionConsentPage(period: Period) extends QuestionPage[Boolean] {
  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "schemeElectionConsent"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(SchemeElectionConsentPage(period)) match {
      case Some(true) =>
        val nextDebitPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, period)
        nextDebitPeriod match {
          case Some(period) => controllers.routes.WhoWillPayController.onPageLoad(NormalMode, period)
          case None         => controllers.routes.AlternativeNameController.onPageLoad(NormalMode)
        }
      case _          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(SchemeElectionConsentPage(period)) match {
      case Some(true) =>
        val nextDebitPeriod: Option[Period] = PeriodService.getNextDebitPeriod(submission, period)
        nextDebitPeriod match {
          case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad()
          case None    => controllers.routes.CheckYourAnswersController.onPageLoad()
        }
      case _          => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
