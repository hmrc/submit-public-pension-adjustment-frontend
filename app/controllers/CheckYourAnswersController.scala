/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers

import com.google.inject.Inject
import controllers.actions._
import models.requests.DataRequest
import models.{Period, UserAnswers}
import pages.ClaimOnBehalfPage
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.PeriodService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.CheckYourAnswersView

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) {
    implicit request =>
      val initialBlock: Seq[Option[SummaryListRow]] = initialRowBlock(request)

      val relevantPeriods: Option[Seq[Period]] =
        request.submission.calculation.map(calc => PeriodService.orderedInDateDebitPeriods(calc))

      val mayBePeriodRowBlock: Option[Seq[Option[SummaryListRow]]] = request.userAnswers.get(ClaimOnBehalfPage) match {
        case Some(claimingOnBehalf) if !claimingOnBehalf => periodRowBlock(relevantPeriods, request.userAnswers)
        case _                                           => None
      }

      val finalBlock: Seq[Option[SummaryListRow]] = finalRowBlock(request)

      val allRows = initialBlock ++ mayBePeriodRowBlock.getOrElse(Seq()) ++ finalBlock

      Ok(view(SummaryListViewModel(allRows.flatten)))
  }

  private def periodRowBlock(relevantPeriods: Option[Seq[Period]], userAnswers: UserAnswers)(implicit
    messages: Messages
  ): Option[Seq[Option[SummaryListRow]]] =
    relevantPeriods.map(periods =>
      periods.flatMap(period =>
        Seq(
          PeriodDetailsSummary.row(period),
          WhoWillPaySummary.row(userAnswers, period),
          WhichPensionSchemeWillPaySummary.row(userAnswers, period),
          PensionSchemeDetailsSummary.row(userAnswers, period),
          AskedPensionSchemeToPayTaxChargeSummary.row(userAnswers, period),
          WhenWillYouAskPensionSchemeToPaySummary.row(userAnswers, period),
          WhenDidYouAskPensionSchemeToPaySummary.row(userAnswers, period)
        )
      )
    )

  private def finalRowBlock(request: DataRequest[AnyContent])(implicit messages: Messages) =
    Seq(
      AlternativeNameSummary.row(request.userAnswers),
      EnterAlternativeNameSummary.row(request.userAnswers),
      ContactNumberSummary.row(request.userAnswers),
      AreYouAUKResidentSummary.row(request.userAnswers),
      UkAddressSummary.row(request.userAnswers),
      InternationalAddressSummary.row(request.userAnswers),
      LegacyPensionSchemeReferenceSummary.row(request.userAnswers),
      ReformPensionSchemeReferenceSummary.row(request.userAnswers),
      ClaimingHigherOrAdditionalTaxRateReliefSummary.row(request.userAnswers),
      HowMuchTaxReliefSummary.row(request.userAnswers),
      WhichPensionSchemeWillPayTaxReliefSummary.row(request.userAnswers),
      BankDetailsSummary.row(request.userAnswers)
    )

  private def initialRowBlock(request: DataRequest[AnyContent])(implicit messages: Messages) =
    Seq(
      ClaimOnBehalfSummary.row(request.userAnswers),
      StatusOfUserSummary.row(request.userAnswers),
      PensionSchemeMemberNameSummary.row(request.userAnswers),
      PensionSchemeMemberDOBSummary.row(request.userAnswers),
      MemberDateOfDeathSummary.row(request.userAnswers),
      PensionSchemeMemberNinoSummary.row(request.userAnswers),
      PensionSchemeMemberTaxReferenceSummary.row(request.userAnswers),
      PensionSchemeMemberResidenceSummary.row(request.userAnswers),
      PensionSchemeMemberUKAddressSummary.row(request.userAnswers),
      PensionSchemeMemberInternationalAddressSummary.row(request.userAnswers)
    )
}
