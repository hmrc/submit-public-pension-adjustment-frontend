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

import connectors.BavfConnector
import controllers.actions.{CalculationDataRequiredAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.bavf.BavfCompleteResponse
import models.requests.DataRequest
import models.{BankDetails, Mode, NavigationState}
import pages._
import pages.navigationObjects.BankDetailsNavigation
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BavfLandingController @Inject() (
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  bavfConnector: BavfConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def redirectBavf(id: String, mode: Mode): Action[AnyContent] = (identify
    andThen getData
    andThen requireCalculationData
    andThen requireData).async { implicit request =>
    bavfHandlerFactory(id, request, mode)
  }

  private def bavfHandlerFactory(id: String, request: DataRequest[AnyContent], mode: Mode)(implicit
    hc: HeaderCarrier
  ) =
    for {
      retrieveAddress <- bavfConnector.retrieveBarsDetails(id)
      updatedAnswers  <- bavfParser(request, retrieveAddress)
      redirectUrl      = BankDetailsNavigation.navigate(updatedAnswers)
      answersWithNav   = NavigationState.save(updatedAnswers, redirectUrl.url)
      _               <- userDataService.set(answersWithNav)
    } yield Redirect(redirectUrl)

  private def bavfParser(
    request: DataRequest[AnyContent],
    completeResponse: BavfCompleteResponse
  ) =
    for {
      answers <- Future.fromTry(
                   request.userAnswers
                     .set(BankDetailsPage, BankDetails.apply(completeResponse))
                 )
    } yield answers
}
