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
import models.{CheckMode, Period, UserAnswers}
import pages.WhenWillYouAskPensionSchemeToPayPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object WhenWillYouAskPensionSchemeToPaySummary {

  def row(answers: UserAnswers, period: Period)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(WhenWillYouAskPensionSchemeToPayPage(period)).map { answer =>
      val value = ValueViewModel(
        HtmlContent(
          HtmlFormat.escape(messages(s"whenWillYouAskPensionSchemeToPay.$answer"))
        )
      )

      SummaryListRowViewModel(
        key = "whenWillYouAskPensionSchemeToPay.checkYourAnswersLabel",
        value = value,
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            routes.WhenWillYouAskPensionSchemeToPayController.onPageLoad(CheckMode, period).url
          )
            .withVisuallyHiddenText(messages("whenWillYouAskPensionSchemeToPay.change.hidden"))
        )
      )
    }
}
