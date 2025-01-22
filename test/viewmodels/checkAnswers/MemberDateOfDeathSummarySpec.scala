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

import models.{CheckMode, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.MemberDateOfDeathPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import scala.xml.Text

class MemberDateOfDeathSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()
  val validAnswer: LocalDate              = LocalDate.of(2019, 4, 6)

  "row" - {
    "when value is entered, return the summary row" in {
      val userAnswers = UserAnswers("id")
        .set(
          MemberDateOfDeathPage,
          validAnswer
        )
        .get
      MemberDateOfDeathSummary.row(userAnswers) shouldBe Some(
        SummaryListRowViewModel(
          key = "memberDateOfDeath.checkYourAnswersLabel",
          value = ValueViewModel(Text("6 April 2019").toString()),
          actions = Seq(
            ActionItemViewModel("site.change", controllers.routes.MemberDateOfDeathController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText("memberDateOfDeath.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      MemberDateOfDeathSummary.row(userAnswers) shouldBe None
    }
  }

}
