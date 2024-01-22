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

import bars.PpaBarsService
import bars.barsmodel.response._
import controllers.actions._
import forms.BankDetailsFormProvider
import forms.BarsOverrides._
import forms.helper.FormErrorWithFieldMessageOverrides
import models.requests.DataRequest
import models.{BankDetails, Mode, NavigationState, UserAnswers}
import pages.BankDetailsPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.BankDetailsView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class BankDetailsController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  ppaBarsService: PpaBarsService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  formProvider: BankDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: BankDetailsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
      val preparedForm = request.userAnswers.get(BankDetailsPage) match {
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
          value => handleValidForm(value, mode, request.userAnswers)
        )
    }

  private def handleFormWithWithBarsError(error: FormErrorWithFieldMessageOverrides, mode: Mode)(implicit
    request: DataRequest[AnyContent]
  ): Future[Result] =
    Future.successful(BadRequest(view(form.withError(error.formError), mode, error.fieldMessageOverrides)))

  private def handleValidForm(value: BankDetails, mode: Mode, userAnswers: UserAnswers)(implicit
    request: DataRequest[AnyContent]
  ): Future[Result] = {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    ppaBarsService.verifyBankDetails(value).flatMap {
      case Right(_) =>
        for {
          updatedAnswers <- Future.fromTry(userAnswers.set(BankDetailsPage, value))
          redirectUrl     = BankDetailsPage.navigate(mode, updatedAnswers).url
          answersWithNav  = NavigationState.save(updatedAnswers, redirectUrl)
          _              <- sessionRepository.set(answersWithNav)
        } yield Redirect(redirectUrl)

      case Left(error) =>
        handleBankDetailsError(mode, error)
    }
  }

  private def handleBankDetailsError(mode: Mode, error: BarsError)(implicit
    request: DataRequest[AnyContent]
  ): Future[Result] =
    error match {
      case ThirdPartyError(resp)              =>
        throw new RuntimeException(s"BARS verify third-party error. BARS response: $resp")
      case AccountNumberNotWellFormatted(_)   =>
        handleFormWithWithBarsError(accountNumberNotWellFormatted, mode)
      case SortCodeNotPresentOnEiscd(_)       =>
        handleFormWithWithBarsError(sortCodeNotPresentOnEiscd, mode)
      case SortCodeOnDenyListErrorResponse(_) =>
        handleFormWithWithBarsError(sortCodeOnDenyList, mode)
      case NameDoesNotMatch(_)                =>
        handleFormWithWithBarsError(nameDoesNotMatch, mode)
      case AccountDoesNotExist(_)             =>
        handleFormWithWithBarsError(accountDoesNotExist, mode)
      case OtherBarsError(_)                  =>
        handleFormWithWithBarsError(otherBarsError, mode)
      case _                                  =>
        handleFormWithWithBarsError(accountNumberNotWellFormatted, mode)
    }
}
