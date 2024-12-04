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
import forms.AreYouAUKResidentFormProvider
import models.{NormalMode, PensionSchemeMemberInternationalAddress, PensionSchemeMemberUKAddress, UkAddress, UserAnswers}
import pages.{PensionSchemeMemberInternationalAddressPage, PensionSchemeMemberUKAddressPage}
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.UserDataService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupLandingController @Inject() (
                                                 userDataService: UserDataService,
                                                 identify: IdentifierAction,
                                                 getData: DataRetrievalAction,
                                                 requireCalculationData: CalculationDataRequiredAction,
                                                 requireData: DataRequiredAction,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 addressLookupConnector: AddressLookupConnector)(implicit ec: ExecutionContext)
extends FrontendBaseController
with I18nSupport {


    def redirectAlternativeName(id: Option[String]): Action[AnyContent] = (identify
      andThen getData
      andThen requireCalculationData
      andThen requireData).async { implicit request =>

      for {
        retrieveAddress <- addressLookupConnector.retrieveAddress(id.get)
        getCountry = retrieveAddress.address.country.get
        updatedAnswers <- if (getCountry.code.equals("GB")) {
          for {
            answers <- Future.fromTry(request.userAnswers.set(PensionSchemeMemberUKAddressPage, PensionSchemeMemberUKAddress.apply(retrieveAddress)))
            cleanedAnswers = answers.remove(PensionSchemeMemberInternationalAddressPage)
          } yield cleanedAnswers
        }
        else {
          for {
            answers <- Future.fromTry(request.userAnswers.set(PensionSchemeMemberInternationalAddressPage, PensionSchemeMemberInternationalAddress.apply(retrieveAddress)))
            cleanedAnswers = answers.remove(PensionSchemeMemberUKAddressPage)
          } yield cleanedAnswers
        }
        _ <- userDataService.set(updatedAnswers.get)
      } yield Redirect(controllers.routes.AlternativeNameController.onPageLoad(NormalMode))
    }


}



//  def redirectCheckYourAnswersForClaimOnBehalf(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
//    //redirect from alf to cya and set user answer for claim on behalf address page
//  }
//
//def redirectCheckYourAnswersForMember(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
//  //redirect from alf to cya and set user answer for member address page
//}
//
//  def redirectLegacySchemeReference(): Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) { implicit request =>
//    ???
//  }


