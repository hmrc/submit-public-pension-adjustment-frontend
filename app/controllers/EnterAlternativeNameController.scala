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
import forms.EnterAlternativeNameFormProvider

import javax.inject.Inject
import models.{Mode, NavigationState, UserAnswers}
import pages.{ClaimOnBehalfPage, EnterAlternativeNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.EnterAlternativeNameView

import scala.concurrent.{ExecutionContext, Future}

class EnterAlternativeNameController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  formProvider: EnterAlternativeNameFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EnterAlternativeNameView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
      val form = formProvider(isClaimOnBehalf(request.userAnswers))

      val preparedForm = request.userAnswers.get(EnterAlternativeNamePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, isClaimOnBehalf(request.userAnswers)))
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      val form = formProvider(isClaimOnBehalf(request.userAnswers))

      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, isClaimOnBehalf(request.userAnswers)))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(EnterAlternativeNamePage, value))
              redirectUrl     =
                EnterAlternativeNamePage.navigate(mode, updatedAnswers).url
              answersWithNav  = NavigationState.save(updatedAnswers, redirectUrl)
              _              <- userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }

  def isClaimOnBehalf(userAnswers: UserAnswers): Boolean =
    userAnswers.get(ClaimOnBehalfPage) match {
      case Some(true) => true
      case None       => false
      case _          => false
    }
}
