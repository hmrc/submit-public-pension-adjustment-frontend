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

package viewmodels.checkAnswers.lifetimeallowance

import models.submission.Submission
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object YearChargePaidSummary {

  def row(submission: Submission)(implicit messages: Messages): Option[SummaryListRow] =
    submission.calculationInputs.lifeTimeAllowance.flatMap { lta =>
      lta.newLifeTimeAllowanceAdditions.yearChargePaid.map { yearPaid =>
        val value = ValueViewModel(
          HtmlContent(HtmlFormat.escape(messages(s"yearChargePaid.$yearPaid")))
        )

        SummaryListRowViewModel(
          key = KeyViewModel(s"yearChargePaid.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = value
        )
      }
    }
}
