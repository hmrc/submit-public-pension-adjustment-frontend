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
import forms.HowMuchTaxReliefFormProvider
import models.{Mode, NavigationState, WhichPensionSchemeWillPayTaxRelief}
import pages.{HowMuchTaxReliefPage, WhichPensionSchemeWillPayTaxReliefPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{SchemeService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.HowMuchTaxReliefView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class HowMuchTaxReliefController @Inject() (
  override val messagesApi: MessagesApi,
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  formProvider: HowMuchTaxReliefFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: HowMuchTaxReliefView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(HowMuchTaxReliefPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode))
    }

  def onSubmit(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      val numberOfSchemes: Int                              = SchemeService.allSchemeDetailsForTaxReliefLength(request.submission.calculationInputs)
      val schemeDetails: WhichPensionSchemeWillPayTaxRelief =
        SchemeService.allSchemeDetailsForTaxRelief(request.submission.calculationInputs)
      val memberCredit                                      = request.submission.calculation.map(_.inDates.map(_.memberCredit).sum).getOrElse(0)
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
          value =>
            if (numberOfSchemes == 1) {
              for {
                updatedAnswers <- Future.fromTry(
                                    request.userAnswers
                                      .set(HowMuchTaxReliefPage, value)
                                      .flatMap(_.set(WhichPensionSchemeWillPayTaxReliefPage, schemeDetails.values.head))
                                  )
                redirectUrl     = HowMuchTaxReliefPage.navigate(mode, updatedAnswers, request.submission).url
                answersWithNav  = NavigationState.save(updatedAnswers, redirectUrl)
                _              <- if (memberCredit > 0) userDataService.set(updatedAnswers) else userDataService.set(answersWithNav)
              } yield Redirect(redirectUrl)
            } else {
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(HowMuchTaxReliefPage, value))
                redirectUrl     = HowMuchTaxReliefPage.navigate(mode, updatedAnswers, request.submission).url
                answersWithNav  = NavigationState.save(updatedAnswers, redirectUrl)
                _              <- userDataService.set(answersWithNav)
              } yield Redirect(redirectUrl)
            }
        )
    }
}
