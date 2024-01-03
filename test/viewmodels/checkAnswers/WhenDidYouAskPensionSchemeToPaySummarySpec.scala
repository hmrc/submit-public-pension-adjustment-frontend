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
import models.{CheckMode, Period, UserAnswers}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.WhenDidYouAskPensionSchemeToPayPage
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import scala.xml.Text

class WhenDidYouAskPensionSchemeToPaySummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()
  val validAnswer: LocalDate              = LocalDate.of(2015, 4, 6)

  "row" - {
    "when user enters date, return the summary row" in {

      val userAnswers = UserAnswers("id")
        .set(
          WhenDidYouAskPensionSchemeToPayPage(Period._2020),
          validAnswer
        )
        .get
      WhenDidYouAskPensionSchemeToPaySummary.row(userAnswers, Period._2020) shouldBe Some(
        SummaryListRowViewModel(
          key = "whenDidYouAskPensionSchemeToPay.checkYourAnswersLabel",
          value = ValueViewModel(Text("6 April 2015").toString()),
          actions = Seq(
            ActionItemViewModel(
              "site.change",
              routes.WhenDidYouAskPensionSchemeToPayController.onPageLoad(CheckMode, Period._2020).url
            )
              .withVisuallyHiddenText("whenDidYouAskPensionSchemeToPay.change.hidden")
          )
        )
      )
    }

    "when answer unavailable, return empty" in {
      val userAnswers = UserAnswers("id")
      WhenDidYouAskPensionSchemeToPaySummary.row(userAnswers, Period._2020) shouldBe None
    }

  }
}
