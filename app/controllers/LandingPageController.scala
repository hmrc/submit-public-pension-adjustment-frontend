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

import connectors.SubmitBackendConnector
import controllers.actions.*
import models.UniqueId
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class LandingPageController @Inject() (
  override val messagesApi: MessagesApi,
  identify: LandingPageIdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  submitBackendConnector: SubmitBackendConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  def onPageLoad(submissionUniqueId: Option[UniqueId] = None): Action[AnyContent] = identify.async { implicit request =>
    val submissionRetrievalStatus: Future[Boolean] = submissionUniqueId match {
      case Some(_) =>
        for {
          _    <- submitBackendConnector.sendSubmissionSignal(submissionUniqueId)
          calc <- submitBackendConnector.sendCalcUserAnswerSignal(submissionUniqueId)
        } yield calc
      case None    => Future.successful(false)
    }
    submissionRetrievalStatus.map { submissionRetrievalStatus =>
      if (submissionRetrievalStatus) {
        Redirect(routes.SubmissionInfoController.onPageLoad())
      } else {
        Redirect(routes.CalculationPrerequisiteController.onPageLoad())
      }
    }
  }
}
