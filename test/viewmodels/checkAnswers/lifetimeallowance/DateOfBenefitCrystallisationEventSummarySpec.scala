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

package viewmodels.checkAnswers.lifetimeallowance

import models.submission.Submission
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.TestData
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

import java.time.LocalDate
import scala.xml.Text

class DateOfBenefitCrystallisationEventSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()
  val validAnswer: LocalDate              = LocalDate.of(2015, 4, 6)

  "row" - {
    "when value is entered, return the summary row" in {
      val lifeTimeAllowance      = TestData.calculationInputs.lifeTimeAllowance.get.copy(
        benefitCrystallisationEventDate = validAnswer
      )
      val calculationInputs      = TestData.calculationInputs.copy(lifeTimeAllowance = Some(lifeTimeAllowance))
      val submission: Submission = Submission("id", "uniqueId", calculationInputs, None)

      DateOfBenefitCrystallisationEventSummary.row(submission) `shouldBe` Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"dateOfBenefitCrystallisationEvent.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(Text("6 April 2015").toString())
        )
      )
    }

    "when answer unavailable, return empty" in {
      val calculationInputs      = TestData.calculationInputs.copy(lifeTimeAllowance = None)
      val submission: Submission = Submission("id", "uniqueId", calculationInputs, None)
      DateOfBenefitCrystallisationEventSummary.row(submission) `shouldBe` None
    }
  }
}
