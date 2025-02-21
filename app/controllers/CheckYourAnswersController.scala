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

package controllers

import com.google.inject.Inject
import controllers.actions._
import models.StatusOfUser.{Deputyship, PowerOfAttorney}
import models.requests.DataRequest
import models.submission.Submission
import models.{NavigationState, PSTR, Period, UserAnswers}
import pages.{ClaimOnBehalfPage, StatusOfUserPage}
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import services.{PeriodService, SchemeService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.{CheckYourAnswersView, IncompleteDataCaptureView}

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  checkYourAnswersView: CheckYourAnswersView,
  incompleteDataCaptureView: IncompleteDataCaptureView
) extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent]                               = (identify andThen getData andThen requireCalculationData andThen requireData) {
    implicit request =>
      if (NavigationState.isDataCaptureComplete(request.userAnswers)) {
        val relevantPeriods: Option[Seq[Period]] =
          request.submission.calculation.map(calc => PeriodService.orderedInDateDebitPeriods(calc))

        val mayBePeriodRowBlock: Option[Seq[Option[SummaryListRow]]] =
          request.userAnswers.get(ClaimOnBehalfPage) match {
            case Some(false) => periodRowBlock(relevantPeriods, request.userAnswers)
            case Some(true)  =>
              request.userAnswers.get(StatusOfUserPage) match {
                case Some(Deputyship | PowerOfAttorney) => periodRowBlock(relevantPeriods, request.userAnswers)
                case _                                  => None
              }
            case _           => None
          }

        val allRows = initialRowBlock(request) ++ mayBePeriodRowBlock.getOrElse(Seq()) ++ finalRowBlock(request)

        Ok(
          checkYourAnswersView(
            SummaryListViewModel(allRows.flatten),
            maybeCreditSchemeConsent(request.submission)
          )
        )
      } else {
        Ok(incompleteDataCaptureView(NavigationState.getContinuationUrl(request.userAnswers)))
      }

  }
  private def maybeCreditSchemeConsent(submission: Submission): Call =
    submission.calculation match {
      case Some(calculation) =>
        if (calculation.inDates.map(_.schemeCredit).sum > 0) {
          controllers.routes.SchemeCreditConsentController.onPageLoad()
        } else {
          controllers.routes.DeclarationsController.onPageLoad
        }
      case None              => controllers.routes.DeclarationsController.onPageLoad
    }

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

  private def finalRowBlock(request: DataRequest[AnyContent])(implicit
    messages: Messages
  ): Seq[Option[SummaryListRow]] =
    Seq(
      AlternativeNameSummary.row(request.userAnswers),
      EnterAlternativeNameSummary.row(request.userAnswers),
      ContactNumberSummary.row(request.userAnswers),
      AreYouAUKResidentSummary.row(request.userAnswers),
      UkAddressSummary.row(request.userAnswers),
      InternationalAddressSummary.row(request.userAnswers)
    ) ++ referenceRowBlock(request) ++ Seq(
      ClaimingHigherOrAdditionalTaxRateReliefSummary.row(request.userAnswers),
      HowMuchTaxReliefSummary.row(request.userAnswers),
      WhichPensionSchemeWillPayTaxReliefSummary.row(request.userAnswers),
      BankDetailsSummary.row(request.userAnswers)
    )

  private def referenceRowBlock(
    request: DataRequest[AnyContent]
  )(implicit messages: Messages): Seq[Option[SummaryListRow]] =
    SchemeService
      .allPensionSchemeDetails(request.submission.calculationInputs)
      .flatMap(psd =>
        Seq(
          ReferenceDetailsSummary.row(psd),
          LegacyPensionSchemeReferenceSummary
            .row(request.userAnswers, PSTR(psd.pensionSchemeTaxReference), psd.pensionSchemeTaxReference),
          ReformPensionSchemeReferenceSummary
            .row(request.userAnswers, PSTR(psd.pensionSchemeTaxReference), psd.pensionSchemeTaxReference)
        )
      )
}
