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

package pages

import models.{CheckMode, NormalMode, PSTR, Period, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class WhichPensionSchemeWillPayPage(period: Period) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "aa" \ "years" \ period.toString \ toString

  override def toString: String = "whichPensionSchemeWillPay"

  override protected def navigateInNormalMode(answers: UserAnswers): Call = {
    val selectedScheme: Option[String] = answers.get(WhichPensionSchemeWillPayPage(period))
    selectedScheme match {
      case Some(PSTR.New)                  =>
        controllers.routes.PensionSchemeDetailsController.onPageLoad(NormalMode, period)
      case Some("Cynllun pensiwn preifat") =>
        controllers.routes.PensionSchemeDetailsController.onPageLoad(NormalMode, period)
      case Some(_)                         => controllers.routes.AskedPensionSchemeToPayTaxChargeController.onPageLoad(NormalMode, period)
      case _                               => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers): Call =
    answers.get(WhichPensionSchemeWillPayPage(period)) match {
      case Some(PSTR.New)                  =>
        controllers.routes.PensionSchemeDetailsController.onPageLoad(CheckMode, period)
      case Some("Cynllun pensiwn preifat") =>
        controllers.routes.PensionSchemeDetailsController.onPageLoad(CheckMode, period)
      case Some(_)                         =>
        if (answers.get(AskedPensionSchemeToPayTaxChargePage(period)).isEmpty)
          controllers.routes.AskedPensionSchemeToPayTaxChargeController.onPageLoad(CheckMode, period)
        else controllers.routes.CheckYourAnswersController.onPageLoad
      case _                               => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }

  override def cleanup(value: Option[String], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some("Private pension scheme")  => super.cleanup(value, userAnswers)
      case Some("Cynllun pensiwn preifat") => super.cleanup(value, userAnswers)
      case Some(_)                         => userAnswers.remove(PensionSchemeDetailsPage(period))
      case None                            => super.cleanup(value, userAnswers)
    }
}
