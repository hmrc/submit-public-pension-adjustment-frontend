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

import connectors.CalculateBackendConnector
import controllers.actions._
import forms.ConfirmEditAnswersFormProvider

import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import pages.ConfirmEditAnswersPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConfirmEditAnswersView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmEditAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  submissionDataService: SubmissionDataService,
  calculateBackendDataService: CalculateBackendDataService,
  calculateBackendConnector: CalculateBackendConnector,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  formProvider: ConfirmEditAnswersFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmEditAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData) { implicit request =>
    val preparedForm = request.userAnswers.getOrElse(UserAnswers(request.userId)).get(ConfirmEditAnswersPage) match {
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
            if (value) {
              for {
                _ <- calculateBackendConnector.sendFlagResetSignal(request.submission.id)
                _ <- userDataService.clear()
                _ <- submissionDataService.clear()
                r <- calculateBackendDataService.clearCalcSubmissionBE()
              } yield r
            }
            for {
              updatedAnswers <-
                Future
                  .fromTry(
                    request.userAnswers.getOrElse(UserAnswers(request.userId)).set(ConfirmEditAnswersPage, value)
                  )
            } yield Redirect(ConfirmEditAnswersPage.navigate(NormalMode, updatedAnswers))
          }
        )
  }
}
