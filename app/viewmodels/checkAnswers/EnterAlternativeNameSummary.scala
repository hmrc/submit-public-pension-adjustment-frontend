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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.{ClaimOnBehalfPage, EnterAlternativeNamePage}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object EnterAlternativeNameSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] = {
    val key =
      if (isClaimOnBehalf(answers)) "enterAlternativeName.checkYourAnswersLabel.onBehalf"
      else "enterAlternativeName.checkYourAnswersLabel"

    answers.get(EnterAlternativeNamePage).map { answer =>
      val value = HtmlFormat.escape(answer).toString
      SummaryListRowViewModel(
        key = key,
        value = ValueViewModel(HtmlContent(value)),
        actions = Seq(
          ActionItemViewModel("site.change", routes.EnterAlternativeNameController.onPageLoad(CheckMode).url)
            .withVisuallyHiddenText(messages("enterAlternativeName.change.hidden"))
        )
      )
    }
  }
  def isClaimOnBehalf(userAnswers: UserAnswers): Boolean                             =
    userAnswers.get(ClaimOnBehalfPage) match {
      case Some(true) => true
      case None       => false
      case _          => false
    }
}
