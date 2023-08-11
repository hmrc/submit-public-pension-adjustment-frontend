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

import controllers.actions._
import models.finalsubmission.AuthRetrievals

import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SubmissionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SubmissionView

import scala.concurrent.ExecutionContext

class SubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: SubmissionView,
  submissionService: SubmissionService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      val authRetrievals = AuthRetrievals(
        request.userId,
        request.nino,
        request.name.map(n => n.name.getOrElse("") + n.lastName.getOrElse("")),
        request.saUtr,
        request.dob
      )

      submissionService
        .sendFinalSubmission(
          authRetrievals,
          request.submission.calculationInputs,
          request.submission.calculation,
          request.userAnswers
        )
        .map { finalSubmissionResponse =>
          Ok(view(finalSubmissionResponse))

        }

    }
}
