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
import pages.PensionSchemeMemberNamePage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import scala.xml.Text

class PensionSchemeMemberNameSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when user enters name, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          PensionSchemeMemberNamePage,
          "John Doe"
        )
        .get
      PensionSchemeMemberNameSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "pensionSchemeMemberName.checkYourAnswersLabel",
          value = ValueViewModel(Text("John Doe").toString()),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.PensionSchemeMemberNameController.onPageLoad(CheckMode).url
            )
              .withVisuallyHiddenText("pensionSchemeMemberName.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      PensionSchemeMemberNameSummary.row(userAnswers) shouldBe None
    }

  }
}
