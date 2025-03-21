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

import config.FrontendAppConfig
import connectors.{CalculateBackendConnector, SubmitBackendConnector}
import controllers.actions.{CalculationDataRequiredAction, DataRetrievalAction, IdentifierAction}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{CalculateBackendDataService, SubmissionDataService, UserDataService}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class EditCalculationController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  calculateBackendConnector: CalculateBackendConnector,
  userDataService: UserDataService,
  submissionDataService: SubmissionDataService,
  submitBackendConnector: SubmitBackendConnector,
  calculateBackendDataService: CalculateBackendDataService,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController {

  def calculateFrontendTaskList = s"${config.calculateFrontend}/task-list"

  def editCalculation: Action[AnyContent] = (identify andThen getData andThen requireCalculationData).async {
    implicit request =>
      for {
        _ <- calculateBackendConnector.updateCalcBEWithUserAnswers(request.submission.uniqueId)
        _ <- calculateBackendConnector.sendFlagResetSignal(request.submission.id)
        _ <- userDataService.clear()
        _ <- submissionDataService.clear()
        _ <- submitBackendConnector.clearCalcUserAnswersSubmitBE()
        _ <- calculateBackendDataService.clearSubmissionCalcBE()
      } yield Redirect(calculateFrontendTaskList)
  }
}
