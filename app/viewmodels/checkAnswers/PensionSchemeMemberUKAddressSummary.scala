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
import pages.PensionSchemeMemberUKAddressPage
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.HtmlContent
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist.*
import viewmodels.implicits.*

object PensionSchemeMemberUKAddressSummary {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(PensionSchemeMemberUKAddressPage).map { answer =>
      val value = Seq(
        answer.organisation.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(answer.addressLine1).toString),
        answer.addressLine2.map(HtmlFormat.escape),
        answer.addressLine3.map(HtmlFormat.escape),
        Some(HtmlFormat.escape(answer.townOrCity)),
        answer.county.map(HtmlFormat.escape),
        answer.postCode.map(HtmlFormat.escape),
        answer.country.map(HtmlFormat.escape)
      ).flatten.mkString("<br/>")

      SummaryListRowViewModel(
        key = "pensionSchemeMemberUKAddress.checkYourAnswersLabel",
        value = ValueViewModel(HtmlContent(value)),
        actions = Seq(
          ActionItemViewModel("site.change", routes.AddressLookupRampOnController.rampOnClaimOnBehalf(CheckMode).url)
            .withVisuallyHiddenText(messages("pensionSchemeMemberUKAddress.change.hidden"))
        )
      )
    }
}
