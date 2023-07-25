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
import models.{CheckMode, UkAddress, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.UkAddressPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class UkAddressSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  val mockAddress = UkAddress("line1", None, "town", None, "AA1 1AA")

  "row" - {
    "when user submits address, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          UkAddressPage,
          mockAddress
        )
        .get

      UkAddressSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "ukAddress.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("line1<br/>town<br/>AA1 1AA")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.UkAddressController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("ukAddress.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      UkAddressSummary.row(userAnswers) shouldBe None
    }
  }
}
