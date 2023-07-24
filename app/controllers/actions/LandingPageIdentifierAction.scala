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

package controllers.actions

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.routes
import models.UniqueId
import models.requests.IdentifierRequest
import play.api.Logging
import play.api.mvc.{ActionBuilder, ActionFunction, AnyContent, BodyParsers, Request, Result}
import play.api.mvc.Results.Redirect
import uk.gov.hmrc.auth.core.{AffinityGroup, AuthConnector, AuthorisationException, AuthorisedFunctions, ConfidenceLevel, NoActiveSession, User}
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait LandingPageIdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent]
    with ActionFunction[Request, IdentifierRequest]

class AuthenticatedLandingPageIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: FrontendAppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends LandingPageIdentifierAction
    with AuthorisedFunctions
    with Logging {

  private val retrievals =
    Retrievals.nino and
      Retrievals.internalId and
      Retrievals.affinityGroup and
      Retrievals.credentialRole and
      Retrievals.name and
      Retrievals.saUtr and
      Retrievals.dateOfBirth

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)

    authorised(AffinityGroup.Individual and ConfidenceLevel.L250).retrieve(retrievals) {
      case Some(nino) ~ Some(userId) ~ Some(AffinityGroup.Individual) ~ Some(User) ~ Some(name) ~ Some(saUtr) ~ Some(
            dob
          ) =>
        block(IdentifierRequest(request, userId, nino, Some(name), Some(saUtr), Some(dob)))
      case Some(nino) ~ Some(userId) ~ Some(AffinityGroup.Individual) ~ Some(User) ~ Some(name) ~ Some(saUtr) ~ None =>
        block(IdentifierRequest(request, userId, nino, Some(name), Some(saUtr), None))
      case Some(nino) ~ Some(userId) ~ Some(AffinityGroup.Individual) ~ Some(User) ~ Some(name) ~ None ~ None        =>
        block(IdentifierRequest(request, userId, nino, Some(name), None, None))
      case _                                                                                                         =>
        logger.warn(s"Incomplete retrievals")
        Future.successful(Redirect(routes.UnauthorisedController.onPageLoad.url))
    } recover {
      case _: NoActiveSession        =>
        noActiveSession(request)
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad)
    }
  }

  private def noActiveSession[A](request: Request[A]) =
    request.getQueryString("submissionUniqueId") match {
      case Some(submissionUniqueId) =>
        UniqueId.fromString(submissionUniqueId) match {
          case Some(Right(Some(UniqueId(_)))) =>
            Redirect(
              config.loginUrl,
              Map("continue" -> Seq(s"${config.landingPageLoginContinueUrl}?submissionUniqueId=$submissionUniqueId"))
            )
          case _                              =>
            logger.error("submissionUniqueId is invalid")
            Redirect(controllers.routes.JourneyRecoveryController.onPageLoad(None))
        }
      case None                     =>
        logger.error("no submissionUniqueId is specified")
        Redirect(routes.CalculationPrerequisiteController.onPageLoad.url)
    }
}