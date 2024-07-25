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

import controllers.actions.{DataRetrievalAction, IdentifierAction}
import models.{NavigationState, SubmissionSaveAndReturnAuditEvent, UserAnswers}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.AuditService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ContinueSessionController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  auditService: AuditService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController {

  def continueSession: Action[AnyContent] = (identify andThen getData).async { implicit request =>
    val userAnswers = request.userAnswers.getOrElse(UserAnswers(request.userId))
    val uniqueId    = request.submission.map(_.uniqueId).getOrElse("Submission Not Found")
    for {
      _ <- auditService.auditSubmissionUserSelectionContinue(
             SubmissionSaveAndReturnAuditEvent(true, uniqueId, request.userId)
           )
    } yield Redirect(NavigationState.getContinuationUrl(userAnswers))
  }
}
