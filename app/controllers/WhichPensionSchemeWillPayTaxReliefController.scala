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
import forms.WhichPensionSchemeWillPayTaxReliefFormProvider
import models.{Mode, NavigationState}
import pages.WhichPensionSchemeWillPayTaxReliefPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{SchemeService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.WhichPensionSchemeWillPayTaxReliefView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhichPensionSchemeWillPayTaxReliefController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  formProvider: WhichPensionSchemeWillPayTaxReliefFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: WhichPensionSchemeWillPayTaxReliefView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(WhichPensionSchemeWillPayTaxReliefPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, SchemeService.allSchemeDetailsForTaxRelief(request.submission.calculationInputs)))
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      val memberCredit = request.submission.calculation.map(_.inDates.map(_.memberCredit).sum).getOrElse(0)
      form
        .bindFromRequest()
        .fold(
          formWithErrors =>
            Future.successful(
              BadRequest(
                view(
                  formWithErrors,
                  mode,
                  SchemeService.allSchemeDetailsForTaxRelief(request.submission.calculationInputs)
                )
              )
            ),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(WhichPensionSchemeWillPayTaxReliefPage, value))
              redirectUrl     =
                WhichPensionSchemeWillPayTaxReliefPage.navigate(mode, updatedAnswers, request.submission).url
              answersWithNav  = NavigationState.save(updatedAnswers, redirectUrl)
              _              <- if (memberCredit > 0) userDataService.set(updatedAnswers) else userDataService.set(answersWithNav)
            } yield Redirect(redirectUrl)
        )
    }
}
