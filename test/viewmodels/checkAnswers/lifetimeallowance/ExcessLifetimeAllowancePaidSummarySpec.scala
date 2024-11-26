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

import models.calculation.inputs.ExcessLifetimeAllowancePaid
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

class ExcessLifetimeAllowancePaidSummarySpec extends AnyFreeSpec with Matchers {

  private implicit val messages: Messages = Helpers.stubMessages()

  "row" - {
    "when Annual payment is selected, return the summary row" in {
      val lifeTimeAllowance = TestData.calculationInputs.lifeTimeAllowance.get.copy(
        previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Annualpayment)
      )
      val calculationInputs = TestData.calculationInputs.copy(lifeTimeAllowance = Some(lifeTimeAllowance))
      val submission: Submission = Submission("id", "uniqueId", calculationInputs, None)

      ExcessLifetimeAllowancePaidSummary.row(submission) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"excessLifetimeAllowancePaid.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("excessLifetimeAllowancePaid.annualPayment"))
        )
      )
    }

    "when Lump sum is selected, return the summary row" in {
      val lifeTimeAllowance = TestData.calculationInputs.lifeTimeAllowance.get.copy(
        previousLifetimeAllowanceChargePaymentMethod = Some(ExcessLifetimeAllowancePaid.Lumpsum)
      )
      val calculationInputs = TestData.calculationInputs.copy(lifeTimeAllowance = Some(lifeTimeAllowance))
      val submission: Submission = Submission("id", "uniqueId", calculationInputs, None)

      ExcessLifetimeAllowancePaidSummary.row(submission) shouldBe Some(
        SummaryListRowViewModel(
          key = KeyViewModel(s"excessLifetimeAllowancePaid.checkYourAnswersLabel").withCssClass(keyCssClass),
          value = ValueViewModel(HtmlContent("excessLifetimeAllowancePaid.lumpSum"))
        )
      )
    }

    "when answer unavailable, return empty" in {
      val calculationInputs = TestData.calculationInputs.copy(lifeTimeAllowance = None)
      val submission: Submission = Submission("id", "uniqueId", calculationInputs, None)
      ExcessLifetimeAllowancePaidSummary.row(submission) shouldBe None
    }
  }
}
