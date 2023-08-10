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

package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, PSTR, UserAnswers}
import pages.LegacyPensionSchemeReferencePage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object LegacyPensionSchemeReferenceSummary {

  def row(answers: UserAnswers, pstr: PSTR, schemeName: String)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(LegacyPensionSchemeReferencePage(pstr, schemeName)).map { answer =>
      val value = if (answer == "") messages("checkYourAnswers.notAnswered") else answer
      SummaryListRowViewModel(
        key = "legacyPensionSchemeReference.checkYourAnswersLabel",
        value = ValueViewModel(HtmlFormat.escape(value).toString),
        actions = Seq(
          ActionItemViewModel(
            "site.change",
            routes.LegacyPensionSchemeReferenceController.onPageLoad(CheckMode, pstr).url
          )
            .withVisuallyHiddenText(messages("legacyPensionSchemeReference.change.hidden"))
        )
      )
    }
}
