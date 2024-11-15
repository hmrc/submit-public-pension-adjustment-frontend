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
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.CurrencyFormatter.currencyFormat
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object LumpSumValueSummary {

  def row(submission: Submission)(implicit messages: Messages): Option[SummaryListRow] =
    submission.calculationInputs.lifeTimeAllowance.flatMap { lta =>
      lta.newLifeTimeAllowanceAdditions.newLumpSumValue.map { lumpSumValue =>
        SummaryListRowViewModel(
          key = KeyViewModel(s"lumpSumValue.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent(currencyFormat(lumpSumValue)))
        )
      }
    }
}
