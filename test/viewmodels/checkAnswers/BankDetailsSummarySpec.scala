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
import pages.BankDetailsPage
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class BankDetailsSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          BankDetailsPage,
          models.BankDetails("Testuser One", "111111", "11111111", None)
        )
        .get
      BankDetailsSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "bankDetails.checkYourAnswersLabel",
          value = ValueViewModel(HtmlContent("Testuser One<br/>111111<br/>11111111")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.BavfRampOnController.rampOnBavf(CheckMode).url
            )
              .withVisuallyHiddenText("bankDetails.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      BankDetailsSummary.row(userAnswers) `shouldBe` None
    }
  }

}
