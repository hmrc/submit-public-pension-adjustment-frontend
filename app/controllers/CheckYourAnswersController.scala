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
import models.finalsubmission.{AuthRetrievals, FinalSubmissionResponse}
import models.requests.DataRequest
import models.{NavigationState, PSTR, Period, UserAnswers, UserSubmissionReference}
import pages.ClaimOnBehalfPage
import play.api.Logging
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.{PeriodService, SchemeService, SubmissionService}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.checkAnswers._
import viewmodels.govuk.summarylist._
import views.html.{CheckYourAnswersView, IncompleteDataCaptureView}

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  submissionService: SubmissionService,
  sessionRepository: SessionRepository,
  checkYourAnswersView: CheckYourAnswersView,
  incompleteDataCaptureView: IncompleteDataCaptureView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) {
    implicit request =>
      if (NavigationState.isDataCaptureComplete(request.userAnswers)) {
        val relevantPeriods: Option[Seq[Period]] =
          request.submission.calculation.map(calc => PeriodService.orderedInDateDebitPeriods(calc))

        val mayBePeriodRowBlock: Option[Seq[Option[SummaryListRow]]] =
          request.userAnswers.get(ClaimOnBehalfPage) match {
            case Some(claimingOnBehalf) if !claimingOnBehalf => periodRowBlock(relevantPeriods, request.userAnswers)
            case _                                           => None
          }

        val allRows = initialRowBlock(request) ++ mayBePeriodRowBlock.getOrElse(Seq()) ++ finalRowBlock(request)

        Ok(checkYourAnswersView(SummaryListViewModel(allRows.flatten)))
      } else {
        Ok(incompleteDataCaptureView(NavigationState.getContinuationUrl(request.userAnswers)))
      }

  }

  def onSubmit(): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      val authRetrievals = AuthRetrievals(
        request.userId,
        request.nino,
        (request.name.givenName.getOrElse("") + " " + request.name.middleName.getOrElse(
          ""
        ) + " " + request.name.familyName.getOrElse("")).trim,
        request.saUtr,
        request.dob
      )

      request.userAnswers.get(UserSubmissionReference()) match {
        case Some(_) =>
          val submissionUniqueId = request.submission.uniqueId
          logger.warn(s"Prevented attempted duplicate submission related to submissionUniqueId : $submissionUniqueId")
          Future.successful(Redirect(controllers.routes.JourneyRecoveryController.onPageLoad()))
        case None    => sendFinalSubmission(request, authRetrievals)
      }
    }

  private def sendFinalSubmission(request: DataRequest[AnyContent], authRetrievals: AuthRetrievals)(implicit
    headerCarrier: HeaderCarrier
  ) =
    submissionService
      .sendFinalSubmission(
        authRetrievals,
        request.submission.calculationInputs,
        request.submission.calculation,
        request.userAnswers
      )
      .map { finalSubmissionResponse =>
        persistSubmissionReference(request, finalSubmissionResponse)
        Redirect(controllers.routes.SubmissionController.onPageLoad())
      }

  private def persistSubmissionReference(
    request: DataRequest[AnyContent],
    finalSubmissionResponse: FinalSubmissionResponse
  ) = {
    val userSubmissionReference: String = finalSubmissionResponse.userSubmissionReference
    val updatedAnswers                  = request.userAnswers.set(UserSubmissionReference(), userSubmissionReference)
    sessionRepository.set(updatedAnswers.get)
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
