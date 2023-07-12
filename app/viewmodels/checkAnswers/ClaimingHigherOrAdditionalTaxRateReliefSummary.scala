package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.ClaimingHigherOrAdditionalTaxRateReliefPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object ClaimingHigherOrAdditionalTaxRateReliefSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(ClaimingHigherOrAdditionalTaxRateReliefPage).map {
      answer =>

        val value = if (answer) "site.yes" else "site.no"

        SummaryListRowViewModel(
          key     = "claimingHigherOrAdditionalTaxRateRelief.checkYourAnswersLabel",
          value   = ValueViewModel(value),
          actions = Seq(
            ActionItemViewModel("site.change", routes.ClaimingHigherOrAdditionalTaxRateReliefController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("claimingHigherOrAdditionalTaxRateRelief.change.hidden"))
          )
        )
    }
}
