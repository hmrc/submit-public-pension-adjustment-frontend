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

import models.UserAnswers
import models.submission.Submission
import play.api.mvc.Call

trait QuestionPageWithLTAOnlyNavigation[A] extends QuestionPage[A] {

  def ltaOnly(submission: Submission): Boolean = {
    val calculationInputs = submission.calculationInputs
    calculationInputs.lifeTimeAllowance.isDefined && calculationInputs.annualAllowance.forall(_.taxYears.isEmpty)
  }

  override def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call =
    if (ltaOnly(submission)) {
      navigateInNormalModeLTAOnly(answers, submission)
    } else {
      navigateInNormalModeAA(answers, submission)
    }

  override def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    if (ltaOnly(submission)) {
      navigateInCheckModeLTAOnly(answers, submission)
    } else {
      navigateInCheckModeAA(answers, submission)
    }

  def navigateInNormalModeAA(answers: UserAnswers, submission: Submission): Call
  def navigateInCheckModeAA(answers: UserAnswers, submission: Submission): Call
  def navigateInNormalModeLTAOnly(answers: UserAnswers, submission: Submission): Call
  def navigateInCheckModeLTAOnly(answers: UserAnswers, submission: Submission): Call
}
