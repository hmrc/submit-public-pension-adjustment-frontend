package viewmodels.checkAnswers

import java.time.format.DateTimeFormatter

import controllers.routes
import models.{CheckMode, UserAnswers}
import pages.MemberDateOfDeathPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import viewmodels.govuk.summarylist._
import viewmodels.implicits._

object MemberDateOfDeathSummary  {

  def row(answers: UserAnswers)(implicit messages: Messages): Option[SummaryListRow] =
    answers.get(MemberDateOfDeathPage).map {
      answer =>

        val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

        SummaryListRowViewModel(
          key     = "memberDateOfDeath.checkYourAnswersLabel",
          value   = ValueViewModel(answer.format(dateFormatter)),
          actions = Seq(
            ActionItemViewModel("site.change", routes.MemberDateOfDeathController.onPageLoad(CheckMode).url)
              .withVisuallyHiddenText(messages("memberDateOfDeath.change.hidden"))
          )
        )
    }
}
