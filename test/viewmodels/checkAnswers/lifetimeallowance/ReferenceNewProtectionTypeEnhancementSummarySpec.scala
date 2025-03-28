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

import models.calculation.inputs.NewEnhancementType
import models.submission.Submission
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.TestData
import play.api.i18n.Messages
import play.api.test.Helpers
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class ReferenceNewProtectionTypeEnhancementSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when value is entered, return the summary row" in {
      val newLifeTimeAllowanceAdditions = TestData.calculationInputs.lifeTimeAllowance.get.newLifeTimeAllowanceAdditions
        .copy(newEnhancementType = Some(NewEnhancementType.InternationalEnhancement))
      val calculationInputs             = TestData.calculationInputs.copy(lifeTimeAllowance =
        Some(
          TestData.calculationInputs.lifeTimeAllowance.get
            .copy(newLifeTimeAllowanceAdditions = newLifeTimeAllowanceAdditions)
        )
      )
      val submission                    = Submission("id", "uniqueId", calculationInputs, None)

      ReferenceNewProtectionTypeEnhancementSummary.row(submission) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"referenceNewProtectionTypeEnhancement.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel("2134567801")
        )
      )
    }

    "when answer unavailable, return empty" in {
      val calculationInputs = TestData.calculationInputs.copy(lifeTimeAllowance = None)
      val submission        = Submission("id", "uniqueId", calculationInputs, None)

      ReferenceNewProtectionTypeEnhancementSummary.row(submission) shouldBe None
    }
  }
}
