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

import controllers.actions._
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import viewmodels.checkAnswers.lifetimeallowance.{AnnualPaymentValueSummary, DateOfBenefitCrystallisationEventSummary, EnhancementTypeSummary, ExcessLifetimeAllowancePaidSummary, InternationalEnhancementReferenceSummary, LifetimeAllowanceChargeSummary, LtaPensionSchemeDetailsSummary, LtaProtectionOrEnhancementsSummary, LumpSumValueSummary, NewAnnualPaymentValueSummary, NewEnhancementTypeSummary, NewExcessLifetimeAllowancePaidSummary, NewInternationalEnhancementReferenceSummary, NewLumpSumValueSummary, NewPensionCreditReferenceSummary, PensionCreditReferenceSummary, ProtectionEnhancedChangedSummary, ProtectionReferenceSummary, ProtectionTypeSummary, QuarterChargePaidSummary, ReferenceNewProtectionTypeEnhancementSummary, SchemeNameAndTaxRefSummary, UserSchemeDetailsSummary, WhatNewProtectionTypeEnhancementSummary, WhoPaidLTAChargeSummary, WhoPayingExtraLtaChargeSummary, YearChargePaidSummary}
import viewmodels.govuk.all.SummaryListViewModel

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class PrintReviewController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: PrintReviewView,
  calculationResultService: CalculationResultService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    val rows: Seq[Option[SummaryListRow]] = Seq(
      DateOfBenefitCrystallisationEventSummary.row(request.submission),
      LtaProtectionOrEnhancementsSummary.row(request.submission),
      ProtectionTypeSummary.row(request.submission),
      ProtectionReferenceSummary.row(request.submission),
      EnhancementTypeSummary.row(request.submission),
      InternationalEnhancementReferenceSummary.row(request.submission),
      PensionCreditReferenceSummary.row(request.submission),
      ProtectionEnhancedChangedSummary.row(request.submission),
      WhatNewProtectionTypeEnhancementSummary.row(request.submission),
      ReferenceNewProtectionTypeEnhancementSummary.row(request.submission),
      NewEnhancementTypeSummary.row(request.submission),
      NewInternationalEnhancementReferenceSummary.row(request.submission),
      NewPensionCreditReferenceSummary.row(request.submission),
      LifetimeAllowanceChargeSummary.row(request.submission),
      ExcessLifetimeAllowancePaidSummary.row(request.submission),
      LumpSumValueSummary.row(request.submission),
      AnnualPaymentValueSummary.row(request.submission),
      WhoPaidLTAChargeSummary.row(request.submission),
      SchemeNameAndTaxRefSummary.row(request.submission),
      UserSchemeDetailsSummary.row(request.submission),
      QuarterChargePaidSummary.row(request.submission),
      YearChargePaidSummary.row(request.submission),
      NewExcessLifetimeAllowancePaidSummary.row(request.submission),
      NewLumpSumValueSummary.row(request.submission),
      NewAnnualPaymentValueSummary.row(request.submission),
      WhoPayingExtraLtaChargeSummary.row(request.submission),
      LtaPensionSchemeDetailsSummary.row(request.submission)
    )

    val isLTACompleteWithoutKickout = LTASection.status(request.userAnswers) == SectionStatus.Completed && !LTASection
      .kickoutHasBeenReached(request.userAnswers)

    calculationResultService.sendRequest(request.userAnswers).flatMap { calculationResponse =>
      val outDatesStringValues = calculationResultService.outDatesSummary(calculationResponse)
      val inDatesStringValues  = calculationResultService.inDatesSummary(calculationResponse)
      val hasinDates: Boolean  = calculationResponse.inDates.isDefinedAt(0)

      calculationResultService
        .calculationReviewIndividualAAViewModel(calculationResponse, None, request.userAnswers)
        .map { calculationReviewIndividualAAViewModel =>
          val isInCredit: Boolean              = calculationResponse.totalAmounts.inDatesCredit > 0
          val isInDebit: Boolean               = calculationResponse.totalAmounts.inDatesDebit > 0
          val includeCompensation2015: Boolean = calculationResponse.totalAmounts.outDatesCompensation > 0
          Ok(
            view(
              form,
              calculationReviewIndividualAAViewModel,
              isInCredit,
              isInDebit,
              outDatesStringValues,
              inDatesStringValues,
              calculationResultService.calculationReviewViewModel(calculationResponse),
              SummaryListViewModel(rows.flatten),
              isLTACompleteWithoutKickout,
              includeCompensation2015,
              controllers.routes.CalculationReviewController.onPageLoad(),
              hasinDates
            )
          )
        }
    }
  }

}
