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
import forms.HowMuchTaxReliefFormProvider

import javax.inject.Inject
import models.{Mode, WhichPensionSchemeWillPayTaxRelief}
import pages.{HowMuchTaxReliefPage, WhichPensionSchemeWillPayTaxReliefPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.SchemeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.HowMuchTaxReliefView

import scala.concurrent.{ExecutionContext, Future}

class HowMuchTaxReliefController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
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
      val numberOfSchemes: Int = SchemeService.allSchemeDetailsForTaxReliefLength(request.submission.calculationInputs)
      val schemeDetails: WhichPensionSchemeWillPayTaxRelief = SchemeService.allSchemeDetailsForTaxRelief(request.submission.calculationInputs)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mode))),
            value =>
              if(numberOfSchemes == 1) {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(HowMuchTaxReliefPage, value)
                  .flatMap(_.set(WhichPensionSchemeWillPayTaxReliefPage, schemeDetails.values.head)))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield Redirect(HowMuchTaxReliefPage.navigate(mode, updatedAnswers, request.submission))
              } else {
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(HowMuchTaxReliefPage, value))
                  _ <- sessionRepository.set(updatedAnswers)
                } yield Redirect(HowMuchTaxReliefPage.navigate(mode, updatedAnswers, request.submission))
              }
          )
    }
}
