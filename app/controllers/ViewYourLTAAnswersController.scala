/*
 * Copyright 2025 HM Revenue & Customs
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
import models.AuthenticatedUserSaveAndReturnAuditEvent
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers.lifetimeallowance._
import viewmodels.govuk.all.SummaryListViewModel
import views.html.ViewYourLTAAnswersView
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ViewYourLTAAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  auditService: AuditService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: ViewYourLTAAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

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

      auditService
        .auditAuthenticatedUserSaveAndReturn(
          AuthenticatedUserSaveAndReturnAuditEvent(
            request.submission.id,
            request.submission.uniqueId,
            true
          )
        ) map { _ =>
        Ok(view(SummaryListViewModel(rows.flatten)))
      }
  }
}
