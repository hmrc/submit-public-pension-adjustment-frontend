/*
 * Copyright 2025 HM Revenue & Customs
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

import config.BavfConfig
import connectors.BavfConnector
import controllers.actions.{CalculationDataRequiredAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{Mode, NormalMode}
import pages.BankDetailsPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class BavfRampOnController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  bavfConnector: BavfConnector,
  bavfConfig: BavfConfig
)(implicit executionContext: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def existingUrlFragmentBavfHandler(mode: Mode): Action[AnyContent] =
    rampOnBavf(mode)

  def rampOnBavf(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      val bankDetailsAnswers         = request.userAnswers.get(BankDetailsPage)

      val returnURL = if (mode == NormalMode) {
        bavfConfig.bavfReturnNormalMode
      } else {
        bavfConfig.bavfReturnCheckMode
      }
      for {
        initialiseBavf <- bavfConnector.initialiseJourney(returnURL, bankDetailsAnswers)(executionContext, hc)
      } yield Redirect(initialiseBavf)
    }
}
