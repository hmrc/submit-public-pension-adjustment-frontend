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
import models.calculation.response.CalculationResponse
import play.api.i18n.{I18nSupport, MessagesApi}
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
      val firstRowBlock: Seq[Option[SummaryListRow]] = Seq(
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

    val mayBePeriodRowBlock: Option[List[Option[SummaryListRow]]] = for {
      submission  <- request.submission
      calculation <- submission.calculation
    } yield PeriodService
      .orderedInDateDebitPeriods(calculation)
      .flatMap(period =>
        Seq(
          PeriodDetailsSummary.row(period),
          WhoWillPaySummary.row(request.userAnswers, period),
          WhichPensionSchemeWillPaySummary.row(request.userAnswers, period),
          PensionSchemeDetailsSummary.row(request.userAnswers, period),
          AskedPensionSchemeToPayTaxChargeSummary.row(request.userAnswers, period),
          WhenWillYouAskPensionSchemeToPaySummary.row(request.userAnswers, period),
          WhenDidYouAskPensionSchemeToPaySummary.row(request.userAnswers, period)
        )
      )

    val finalRowBlock: Seq[Option[SummaryListRow]] = Seq(
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

    val periodRowBlock = mayBePeriodRowBlock.getOrElse(Seq())

    val allRows = firstRowBlock ++ periodRowBlock ++ finalRowBlock

    Ok(view(SummaryListViewModel(allRows.flatten)))
  }
}
