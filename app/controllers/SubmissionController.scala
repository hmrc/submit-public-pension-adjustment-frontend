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
import models.UserSubmissionReference
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SubmissionView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submissionDataService: SubmissionDataService,
  calculateBackendDataService: CalculateBackendDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: SubmissionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      request.userAnswers.get(UserSubmissionReference()) match {
        case Some(usr) =>
          for {
            _ <- userDataService.clear()
            _ <- submissionDataService.clear()
            _ <- calculateBackendDataService.clearCalcUserAnswersBE()
            _ <- calculateBackendDataService.clearCalcSubmissionBE()
          } yield Ok(view(usr, controllers.auth.routes.AuthController.signOut.url))

        case None => Future.successful(Redirect(routes.JourneyRecoveryController.onPageLoad()))
      }
    }

}
