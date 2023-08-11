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
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.LegacyPensionSchemeReferencePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import scala.xml.Text

class LegacyPensionSchemeReferenceSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          LegacyPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"),
          "QT123456123456"
        )
        .get
      LegacyPensionSchemeReferenceSummary.row(userAnswers, PSTR("12345678AB"), "Scheme1") shouldBe Some(
        SummaryListRowViewModel(
          key = "legacyPensionSchemeReference.checkYourAnswersLabel",
          value = ValueViewModel(Text("QT123456123456").toString()),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.LegacyPensionSchemeReferenceController.onPageLoad(CheckMode, PSTR("12345678AB")).url
            )
              .withVisuallyHiddenText("legacyPensionSchemeReference.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
        .set(
          LegacyPensionSchemeReferencePage(PSTR("12345678AB"), "Scheme1"),
          ""
        )
        .get
      LegacyPensionSchemeReferenceSummary.row(userAnswers, PSTR("12345678AB"), "Scheme1") shouldBe Some(
        SummaryListRowViewModel(
          key = "legacyPensionSchemeReference.checkYourAnswersLabel",
          value = ValueViewModel(Text(messages("checkYourAnswers.notAnswered")).toString()),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.LegacyPensionSchemeReferenceController.onPageLoad(CheckMode, PSTR("12345678AB")).url
            )
              .withVisuallyHiddenText("legacyPensionSchemeReference.change.hidden")
          )
        )
      )
    }
  }

}
