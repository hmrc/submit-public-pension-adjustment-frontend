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

import controllers.actions.*
import models.UserSubmissionReference
import models.finalsubmission.{AuthRetrievals, FinalSubmissionResponse}
import models.requests.DataRequest
import play.api.i18n.Lang.logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{SubmissionService, UserDataService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import views.html.SubmissionWaitingRoomView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionWaitingRoomController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  requireCalculationData: CalculationDataRequiredAction,
  view: SubmissionWaitingRoomView,
  submissionService: SubmissionService,
  userDataService: UserDataService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      Ok(view())

      val authRetrievals = AuthRetrievals(
        request.userId,
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
        case None    =>
          sendFinalSubmission(request, authRetrievals)
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
    val hc                              = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    val userSubmissionReference: String = finalSubmissionResponse.userSubmissionReference
    val updatedAnswers                  = request.userAnswers.set(UserSubmissionReference(), userSubmissionReference)
    userDataService.set(updatedAnswers.get)(hc)
  }
}
