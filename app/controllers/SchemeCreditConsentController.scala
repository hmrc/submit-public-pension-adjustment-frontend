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
import models.SchemeCreditConsent
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SchemeCreditConsentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SchemeCreditConsentController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireData: DataRequiredAction,
  userDataService: UserDataService,
  val controllerComponents: MessagesControllerComponents,
  requireCalculationData: CalculationDataRequiredAction,
  view: SchemeCreditConsentView
)(implicit val executionContext: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) {
    implicit request =>
      Ok(view())
  }

  def onSubmit(): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      for {
        userAnswers <- Future.fromTry(request.userAnswers.set(SchemeCreditConsent, true))
        _           <- userDataService.set(userAnswers)
      } yield Redirect(controllers.routes.DeclarationsController.onPageLoad.url)
    }
}
