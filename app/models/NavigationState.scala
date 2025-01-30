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

package models

import play.api.libs.json.JsPath
import queries.{Gettable, Settable}

object NavigationState {

  val dataCaptureStartUrl: String = controllers.routes.ClaimOnBehalfController.onPageLoad(NormalMode).url
  val checkYourAnswersUrl: String = controllers.routes.CheckYourAnswersController.onPageLoad().url
  val declarations: String        = controllers.routes.DeclarationsController.onPageLoad.url

  def isDataCaptureComplete(answers: UserAnswers): Boolean =
    answers.get(NavigationState()) match {
      case Some(`checkYourAnswersUrl`) => true
      case Some(`declarations`)        => true
      case _                           => false
    }

  def getContinuationUrl(answers: UserAnswers): String =
    answers.get(NavigationState()).getOrElse(dataCaptureStartUrl)

  def save(answers: UserAnswers, urlFragment: String): UserAnswers = answers.set(NavigationState(), urlFragment).get

}
case class NavigationState() extends Gettable[String] with Settable[String] {
  override def path: JsPath = JsPath \ "navigation"
}
