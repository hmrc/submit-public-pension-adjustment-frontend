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
import mappers.CalculationResultsMapper
import models.AuthenticatedUserSaveAndReturnAuditEvent
import play.api.data.Form
import play.api.data.Forms.ignored
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import viewmodels.checkAnswers.lifetimeallowance._
import viewmodels.govuk.all.SummaryListViewModel
import views.html.PrintReviewView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PrintReviewController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: PrintReviewView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = Form("_" -> ignored(()))

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireCalculationData).async {
    implicit request =>
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

      val calculationInputs = request.submission.calculationInputs
      if (calculationInputs.annualAllowance.isEmpty) {
        Future.successful(Redirect(controllers.routes.ViewYourLTAAnswersController.onPageLoad()))
      } else {
        val calculation = request.submission.calculation.get

        val isLTAComplete = calculationInputs.lifeTimeAllowance.isDefined

        val outDatesStringValues = CalculationResultsMapper.outDatesSummary(calculation)
        val inDatesStringValues  = CalculationResultsMapper.inDatesSummary(calculation)
        val hasinDates: Boolean  = calculation.inDates.isDefinedAt(0)

        val isInCredit: Boolean              = calculation.totalAmounts.inDatesCredit > 0
        val isInDebit: Boolean               = calculation.totalAmounts.inDatesDebit > 0
        val includeCompensation2015: Boolean = calculation.totalAmounts.outDatesCompensation > 0

        val calculationReviewIndividualAAViewModel = CalculationResultsMapper
          .calculationReviewIndividualAAViewModel(calculation, None, calculationInputs)

        val calculationReviewViewModel = CalculationResultsMapper.calculationReviewViewModel(calculation)

        auditService
          .auditAuthenticatedUserSaveAndReturn(
            AuthenticatedUserSaveAndReturnAuditEvent(
              request.submission.id,
              request.submission.uniqueId,
              true
            )
          ) map { _ =>
          Ok(
            view(
              form,
              calculationReviewIndividualAAViewModel,
              isInCredit,
              isInDebit,
              outDatesStringValues,
              inDatesStringValues,
              calculationReviewViewModel,
              SummaryListViewModel(rows.flatten),
              isLTAComplete,
              includeCompensation2015,
              controllers.routes.ContinueChoiceController.onPageLoad(),
              hasinDates
            )
          )
        }
      }
  }
}
