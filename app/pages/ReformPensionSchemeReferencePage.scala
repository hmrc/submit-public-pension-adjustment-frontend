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

import models.submission.Submission
import models.{NormalMode, PSTR, PensionSchemeDetails, UserAnswers}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import services.SchemeService

case class ReformPensionSchemeReferencePage(pstr: PSTR, schemeName: String) extends QuestionPage[String] {

  override def path: JsPath = JsPath \ "aa" \ "schemes" \ pstr.value \ toString

  override def toString: String = "reformPensionSchemeReference"

  override protected def navigateInNormalMode(answers: UserAnswers, submission: Submission): Call = {
    val allschemeDetails: Seq[PensionSchemeDetails] =
      SchemeService.allPensionSchemeDetails(submission.calculationInputs)
    answers.get(
      ReformPensionSchemeReferencePage(pstr, SchemeService.schemeName(pstr, submission.calculationInputs))
    ) match {
      case Some(_) =>
        val lastPstr = allschemeDetails.last.pensionSchemeTaxReference
        if (pstr.value == lastPstr) {
          controllers.routes.ClaimingHigherOrAdditionalTaxRateReliefController.onPageLoad(NormalMode)
        } else {
          val allPstrs = allschemeDetails.map(psd => psd.pensionSchemeTaxReference)
          val index    = allPstrs.indexOf(pstr.value)
          val nextPstr = allPstrs(index + 1)
          controllers.routes.LegacyPensionSchemeReferenceController
            .onPageLoad(NormalMode, PSTR(nextPstr))
        }

      case _ => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
  }

  override protected def navigateInCheckMode(answers: UserAnswers, submission: Submission): Call =
    answers.get(
      ReformPensionSchemeReferencePage(pstr, SchemeService.schemeName(pstr, submission.calculationInputs))
    ) match {
      case Some(_) => controllers.routes.CheckYourAnswersController.onPageLoad()
      case _       => controllers.routes.JourneyRecoveryController.onPageLoad(None)
    }
}
