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

package pages

import models.{CheckMode, NormalMode, Period, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AskedPensionSchemeToPayTaxChargePage(period: Period) extends QuestionPage[Boolean] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "askedPensionSchemeToPayTaxCharge"

  override protected def navigateInNormalMode(answers: UserAnswers): Call =
    answers.get(AskedPensionSchemeToPayTaxChargePage(period)) match {
      case Some(true)  => controllers.routes.WhenDidYouAskPensionSchemeToPayController.onPageLoad(NormalMode, period)
      case Some(false) => controllers.routes.WhenWillYouAskPensionSchemeToPayController.onPageLoad(NormalMode, period)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(AskedPensionSchemeToPayTaxChargePage(period)) match {
      case Some(true)  => controllers.routes.WhenDidYouAskPensionSchemeToPayController.onPageLoad(CheckMode, period)
      case Some(false) => controllers.routes.WhenWillYouAskPensionSchemeToPayController.onPageLoad(CheckMode, period)
      case _           => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value
      .map {
        case false => userAnswers.remove(WhenDidYouAskPensionSchemeToPayPage(period))
        case true  => userAnswers.remove(WhenWillYouAskPensionSchemeToPayPage(period))
      }
      .getOrElse(super.cleanup(value, userAnswers))
}
