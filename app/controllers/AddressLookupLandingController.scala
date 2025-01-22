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

import connectors.AddressLookupConnector
import controllers.actions.{CalculationDataRequiredAction, DataRequiredAction, DataRetrievalAction, IdentifierAction}
import models.requests.{AddressLookupConfirmation, DataRequest}
import models.{InternationalAddress, Mode, NavigationState, StatusOfUser, UkAddress, UserAnswers}
import pages.navigationObjects.{ClaimOnBehalfPostALFNavigation, UserAddressPostALFNavigation}
import pages._
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.{ClaimOnBehalfNavigationLogicService, PeriodService, UserDataService}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class AddressLookupLandingController @Inject() (
  userDataService: UserDataService,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  addressLookupConnector: AddressLookupConnector
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def redirectClaimOnBehalf(id: Option[String], mode: Mode): Action[AnyContent] = (identify
    andThen getData
    andThen requireCalculationData
    andThen requireData).async { implicit request =>
    claimOnBehalfAddressHandlerFactory(id, request, mode)
  }

  def redirectUserAddress(id: Option[String], mode: Mode): Action[AnyContent] = (identify
    andThen getData
    andThen requireCalculationData
    andThen requireData).async { implicit request =>
    userAddressHandlerFactory(id, request, mode)
  }

  private def claimOnBehalfAddressHandlerFactory(id: Option[String], request: DataRequest[AnyContent], mode: Mode)(
    implicit hc: HeaderCarrier
  ) =
    id match {
      case None          =>
        Future.successful(Redirect(controllers.routes.JourneyRecoveryController.onPageLoad(None)))
      case Some(validId) =>
        for {
          retrieveAddress <- addressLookupConnector.retrieveAddress(validId)
          updatedAnswers  <- addressLocaleParserClaimOnBehalf(request, retrieveAddress)
          cleanedAnswers   = maybeDebitLoopCleanup(updatedAnswers)
          redirectUrl      = ClaimOnBehalfPostALFNavigation.navigate(cleanedAnswers.get, request.submission, mode)
          answersWithNav   = NavigationState.save(cleanedAnswers.get, redirectUrl.url)
          _               <- userDataService.set(answersWithNav)
        } yield Redirect(redirectUrl)
    }

  private def addressLocaleParserClaimOnBehalf(
    request: DataRequest[AnyContent],
    retrieveAddress: AddressLookupConfirmation
  ) = {
    val getCountry = retrieveAddress.address.country
    if (getCountry.code.equals("GB")) {
      for {
        answers       <- Future.fromTry(
                           request.userAnswers
                             .set(PensionSchemeMemberUKAddressPage, UkAddress.apply(retrieveAddress))
                         )
        answers2       = answers.remove(PensionSchemeMemberInternationalAddressPage).get
        cleanedAnswers = answers2.remove(PensionSchemeMemberResidencePage)
      } yield cleanedAnswers
    } else {
      for {
        answers       <- Future.fromTry(
                           request.userAnswers.set(
                             PensionSchemeMemberInternationalAddressPage,
                             InternationalAddress.apply(retrieveAddress)
                           )
                         )
        answers2       = answers.remove(PensionSchemeMemberUKAddressPage).get
        cleanedAnswers = answers2.remove(PensionSchemeMemberResidencePage)
      } yield cleanedAnswers
    }
  }

  private def userAddressHandlerFactory(id: Option[String], request: DataRequest[AnyContent], mode: Mode)(implicit
    hc: HeaderCarrier
  ) =
    id match {
      case None          =>
        Future.successful(Redirect(controllers.routes.JourneyRecoveryController.onPageLoad(None)))
      case Some(validId) =>
        for {
          retrieveAddress <- addressLookupConnector.retrieveAddress(validId)
          updatedAnswers  <- addressLocaleParserUserAddress(request, retrieveAddress)
          redirectUrl      = UserAddressPostALFNavigation.navigate(request.submission, mode)
          answersWithNav   = NavigationState.save(updatedAnswers.get, redirectUrl.url)
          _               <- userDataService.set(answersWithNav)
        } yield Redirect(redirectUrl)
    }

  private def addressLocaleParserUserAddress(
    request: DataRequest[AnyContent],
    retrieveAddress: AddressLookupConfirmation
  ) = {
    val getCountry = retrieveAddress.address.country
    if (getCountry.code.equals("GB")) {
      for {
        answers       <- Future.fromTry(
                           request.userAnswers
                             .set(UkAddressPage, UkAddress.apply(retrieveAddress))
                         )
        answers2       = answers.remove(InternationalAddressPage).get
        cleanedAnswers = answers2.remove(AreYouAUKResidentPage)
      } yield cleanedAnswers
    } else {
      for {
        answers       <- Future.fromTry(
                           request.userAnswers.set(
                             InternationalAddressPage,
                             InternationalAddress.apply(retrieveAddress)
                           )
                         )
        answers2       = answers.remove(UkAddressPage).get
        cleanedAnswers = answers2.remove(AreYouAUKResidentPage)
      } yield cleanedAnswers
    }
  }

  private def maybeDebitLoopCleanup(updatedAnswers: Try[UserAnswers]) = {
    val periodsToCleanup = PeriodService.allInDateRemedyPeriods
    if (updatedAnswers.get.get(StatusOfUserPage).contains(StatusOfUser.LegalPersonalRepresentative)) {
      Try(ClaimOnBehalfNavigationLogicService.periodPageCleanup(updatedAnswers.get, periodsToCleanup))
    } else {
      updatedAnswers
    }
  }
}
