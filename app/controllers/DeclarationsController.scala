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

import controllers.actions._
import models.UserAnswers
import pages.{ClaimOnBehalfPage, PensionSchemeMemberNamePage}
import javax.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DeclarationsView

class DeclarationsController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  getData: DataRetrievalAction,
  requireCalculationData: CalculationDataRequiredAction,
  requireData: DataRequiredAction,
  val controllerComponents: MessagesControllerComponents,
  view: DeclarationsView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = (identify andThen getData andThen requireCalculationData andThen requireData) {
    implicit request =>
      val memberName: String = request.userAnswers.get(PensionSchemeMemberNamePage).getOrElse("")
      Ok(view(isClaimOnBehalf(request.userAnswers), memberName))
  }

  def isClaimOnBehalf(userAnswers: UserAnswers): Boolean =
    userAnswers.get(ClaimOnBehalfPage) match {
      case Some(true) => true
      case None       => false
      case _          => false
    }

}
