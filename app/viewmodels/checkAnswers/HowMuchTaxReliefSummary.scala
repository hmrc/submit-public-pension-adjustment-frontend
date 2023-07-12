package viewmodels.checkAnswers

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.HowMuchTaxReliefPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object HowMuchTaxReliefSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(HowMuchTaxReliefPage).map {
      answer =>

        SummaryListRowViewModel(
          key     = "howMuchTaxRelief.checkYourAnswersLabel",
          value   = ValueViewModel(answer.toString),
          actions = Seq(
            ActionItemViewModel("site.change", routes.HowMuchTaxReliefController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("howMuchTaxRelief.change.hidden"))
          )
        )
    }
}
