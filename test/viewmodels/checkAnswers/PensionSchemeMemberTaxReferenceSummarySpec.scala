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
import pages.PensionSchemeMemberTaxReferencePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class PensionSchemeMemberTaxReferenceSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  val mockUTR = "1234567890"

  "row" - {
    "when user submits UTR, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          PensionSchemeMemberTaxReferencePage,
          mockUTR
        )
        .get

      PensionSchemeMemberTaxReferenceSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "pensionSchemeMemberTaxReference.checkYourAnswersLabel",
          value = ValueViewModel("1234567890"),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.PensionSchemeMemberTaxReferenceController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("pensionSchemeMemberTaxReference.change.hidden")
          )
        )
      )
    }

    "when user does not submit UTR, return the summary row with not answered text" in {

      val userAnswers = UserAnswers("id")
        .set(
          PensionSchemeMemberTaxReferencePage,
          ""
        )
        .get

      PensionSchemeMemberTaxReferenceSummary.row(userAnswers) `shouldBe` Some(
        SummaryListRowViewModel(
          key = "pensionSchemeMemberTaxReference.checkYourAnswersLabel",
          value = ValueViewModel(Messages("checkYourAnswers.notAnswered")),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.PensionSchemeMemberTaxReferenceController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("pensionSchemeMemberTaxReference.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      PensionSchemeMemberTaxReferenceSummary.row(userAnswers) `shouldBe` None
    }
  }
}
