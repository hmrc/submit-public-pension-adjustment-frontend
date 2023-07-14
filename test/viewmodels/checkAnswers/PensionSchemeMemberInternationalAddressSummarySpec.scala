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
import models.{CheckMode, PensionSchemeMemberInternationalAddress, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.PensionSchemeMemberInternationalAddressPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class PensionSchemeMemberInternationalAddressSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  val mockAddress = PensionSchemeMemberInternationalAddress("line1", None, "town", None, None, "France")

  "row" - {
    "when user submits address, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          PensionSchemeMemberInternationalAddressPage,
          mockAddress
        )
        .get

      PensionSchemeMemberInternationalAddressSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "pensionSchemeMemberInternationalAddress.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("line1<br/>town<br/>France")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.PensionSchemeMemberInternationalAddressController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("pensionSchemeMemberInternationalAddress.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      PensionSchemeMemberInternationalAddressSummary.row(userAnswers) shouldBe None
    }
  }
}
