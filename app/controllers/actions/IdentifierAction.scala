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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.Results.*
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  private val retrievals =
    Retrievals.nino and
      Retrievals.affinityGroup and
      Retrievals.credentialRole and
      Retrievals.itmpName and
      Retrievals.saUtr and
      Retrievals.itmpDateOfBirth

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised((AffinityGroup.Individual or AffinityGroup.Organisation) and ConfidenceLevel.L250).retrieve(retrievals) {
      case Some(nino) ~ Some(AffinityGroup.Individual) ~ Some(User) ~ Some(name) ~ saUtr ~ dob   =>
        block(IdentifierRequest(request, nino, name, saUtr, dob))
      case Some(nino) ~ Some(AffinityGroup.Organisation) ~ Some(User) ~ Some(name) ~ saUtr ~ dob =>
        block(IdentifierRequest(request, nino, name, saUtr, dob))
      case _                                                                                     =>
        logger.warn(s"Incomplete retrievals")
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad.url))
    } recover {
      case _: NoActiveSession           =>
        Redirect(config.redirectToStartPage)
      case ex: UnsupportedAffinityGroup =>
        logger.warn(s"User has UnsupportedAffinityGroup. The reason is ${ex.reason} .")
        Redirect(routes.CannotUseServiceNotIndividualController.onPageLoad)
      case ex: AuthorisationException   =>
        logger.warn(s"User has AuthorisationException. The reason is ${ex.reason} .")
        Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }
}
