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

package controllers.actions

import controllers.routes
import models.requests.{CalculationDataRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CalculationDataRequiredActionImpl @Inject() (implicit val executionContext: ExecutionContext)
    extends CalculationDataRequiredAction {

  override protected def refine[A](request: OptionalDataRequest[A]): Future[Either[Result, CalculationDataRequest[A]]] =
    request.submission match {
      case None             =>
        Future.successful(Left(Redirect(routes.CalculationPrerequisiteController.onPageLoad().url)))
      case Some(submission) =>
        Future.successful(
          Right(
            CalculationDataRequest(
              request.request,
              request.userId,
              request.name,
              request.saUtr,
              request.dob,
              submission,
              request.userAnswers
            )
          )
        )

    }

}

trait CalculationDataRequiredAction extends ActionRefiner[OptionalDataRequest, CalculationDataRequest]
