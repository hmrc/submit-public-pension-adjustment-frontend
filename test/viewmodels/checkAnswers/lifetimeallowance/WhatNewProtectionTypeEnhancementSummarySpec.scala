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

import models.calculation.inputs.{CalculationInputs, NewEnhancementType, NewLifeTimeAllowanceAdditions}
import models.calculation.inputs.WhatNewProtectionTypeEnhancement.PrimaryProtection
import models.submission.Submission
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import pages.TestData
import play.api.i18n.Messages
import play.api.test.Helpers
import uk.gov.hmrc.govukfrontend.views.Aliases.HtmlContent
import viewmodels.checkAnswers.FormatUtils.keyCssClass
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

class WhatNewProtectionTypeEnhancementSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when a radio button is selected, return the summary row" in {
      val newLifeTimeAllowanceAdditions: NewLifeTimeAllowanceAdditions =
        TestData.calculationInputs.lifeTimeAllowance.get.newLifeTimeAllowanceAdditions
          .copy(newEnhancementType = Some(NewEnhancementType.InternationalEnhancement))
      val calculationInputs: CalculationInputs                         = TestData.calculationInputs.copy(lifeTimeAllowance =
        Some(
          TestData.calculationInputs.lifeTimeAllowance.get
            .copy(newLifeTimeAllowanceAdditions = newLifeTimeAllowanceAdditions)
        )
      )
      val submission: Submission                                       = Submission("id", "uniqueId", calculationInputs, None)

      WhatNewProtectionTypeEnhancementSummary.row(submission) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"whatNewProtectionTypeEnhancement.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("whatNewProtectionTypeEnhancement.internationalEnhancement"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val calculationInputs = TestData.calculationInputs.copy(lifeTimeAllowance = None)
      val submission        = Submission("id", "uniqueId", calculationInputs, None)

      WhatNewProtectionTypeEnhancementSummary.row(submission) shouldBe None
    }
  }
}
