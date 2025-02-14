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
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.{ClaimOnBehalfPage, EnterAlternativeNamePage}
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import scala.xml.Text

class EnterAlternativeNameSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when user enters name, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          EnterAlternativeNamePage,
          "John Doe"
        )
        .get
      EnterAlternativeNameSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "enterAlternativeName.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("John Doe")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.EnterAlternativeNameController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("enterAlternativeName.change.hidden")
          )
        )
      )
    }

    "when user enters name on behalf of customer, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          EnterAlternativeNamePage,
          "John Doe"
        )
        .get
        .set(
          ClaimOnBehalfPage,
          true
        )
        .get

      EnterAlternativeNameSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "enterAlternativeName.checkYourAnswersLabel.onBehalf",
          value = ValueViewModel(HtmlContent("John Doe")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.EnterAlternativeNameController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("enterAlternativeName.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      EnterAlternativeNameSummary.row(userAnswers) shouldBe None
    }

  }
}
