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

import connectors.SubmitBackendConnector
import controllers.actions.*
import forms.ConfirmRestartAnswersFormProvider
import models.{NormalMode, UserAnswers}
import pages.ConfirmRestartAnswersPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmRestartAnswersView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRestartAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submissionDataService: SubmissionDataService,
  calculateBackendDataService: CalculateBackendDataService,
  submitBackendConnector: SubmitBackendConnector,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  formProvider: ConfirmRestartAnswersFormProvider,
  val controllerComponents: MessagesControllerComponents,
  requireCalculationData: CalculationDataRequiredAction,
  view: ConfirmRestartAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData) { implicit request =>
    val preparedForm = request.userAnswers.getOrElse(UserAnswers(request.userId)).get(ConfirmRestartAnswersPage) match {
      case None        => form
      case Some(value) => form.fill(value)
    }

    Ok(view(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value => {
            val maybeClearF =
              if (value) {
                for {
                  _ <- userDataService.clear()
                  _ <- submissionDataService.clear()
                  _ <- calculateBackendDataService.clearSubmissionCalcBE()
                  _ <- submitBackendConnector.clearCalcUserAnswersSubmitBE()
                  _ <- calculateBackendDataService.clearUserAnswersCalcBE()
                } yield ()
              } else {
                Future.unit
              }
            for {
              _              <- maybeClearF
              updatedAnswers <-
                Future.fromTry(
                  request.userAnswers.getOrElse(UserAnswers(request.userId)).set(ConfirmRestartAnswersPage, value)
                )
            } yield Redirect(ConfirmRestartAnswersPage.navigate(NormalMode, updatedAnswers))
          }
        )
  }
}
