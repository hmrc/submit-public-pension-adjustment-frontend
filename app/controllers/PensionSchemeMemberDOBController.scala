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
import forms.PensionSchemeMemberDOBFormProvider

import javax.inject.Inject
import models.{Mode, NavigationState}
import pages.PensionSchemeMemberDOBPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.PensionSchemeMemberDOBView

import scala.concurrent.{ExecutionContext, Future}

class PensionSchemeMemberDOBController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  formProvider: PensionSchemeMemberDOBFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: PensionSchemeMemberDOBView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(PensionSchemeMemberDOBPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(PensionSchemeMemberDOBPage, value))
              redirectUrl =
                PensionSchemeMemberDOBPage.navigate(mode, updatedAnswers).url
              answersWithNav = NavigationState.save(updatedAnswers, redirectUrl)
              _ <- sessionRepository.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}
