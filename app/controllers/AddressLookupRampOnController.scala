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

import config.{ALFConfig, FrontendAppConfig}
import connectors.AddressLookupConnector
import controllers.actions.{CalculationDataRequiredAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.{Mode, NormalMode}
import models.requests.{AddressLookupOptions, AddressLookupRequest, TimeoutConfig}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupRampOnController @Inject() (
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  addressLookupConnector: AddressLookupConnector,
  frontendAppConfig: FrontendAppConfig,
  ALFConfig: ALFConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def existingUrlFragmentClaimOnBehalfHandler(mode: Mode): Action[AnyContent] =
    rampOnClaimOnBehalf(mode)

  def existingUrlFragmentUserAddressHandler(mode: Mode): Action[AnyContent] =
    rampOnUserAddress(mode)

  def rampOnClaimOnBehalf(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      val returnURL                  = if (mode == NormalMode) {
        ALFConfig.addressLookupReturnClaimOnBehalfNormalMode
      } else {
        ALFConfig.addressLookupReturnClaimOnBehalfCheckMode
      }
      val alfRequest                 = requestBuilder(true, returnURL)
      for {
        initialiseALF <- addressLookupConnector.initialiseJourney(alfRequest)
      } yield Redirect(initialiseALF)
    }

  def rampOnUserAddress(mode: Mode): Action[AnyContent] =
    (identify andThen getData andThen requireCalculationData andThen requireData).async { implicit request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      val returnURL                  = if (mode == NormalMode) {
        ALFConfig.addressLookupReturnUserAddressNormalMode
      } else {
        ALFConfig.addressLookupReturnUserAddressCheckMode
      }
      val alfRequest                 = requestBuilder(false, returnURL)
      for {
        initialiseALF <- addressLookupConnector.initialiseJourney(alfRequest)
      } yield Redirect(initialiseALF)
    }

  private def requestBuilder(isClaimOnBehalf: Boolean, returnURL: String): AddressLookupRequest =
    AddressLookupRequest(
      2,
      AddressLookupOptions(
        continueUrl = returnURL,
        timeoutConfig = Option(TimeoutConfig(900, frontendAppConfig.signOutUrl, Some(ALFConfig.keepAlive))),
        disableTranslations = true
      )
    )
}
