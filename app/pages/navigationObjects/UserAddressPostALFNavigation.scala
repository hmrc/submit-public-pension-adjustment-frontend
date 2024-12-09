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

package pages.navigationObjects

import models.{Mode, NormalMode, PSTR}
import models.submission.Submission
import play.api.mvc.Call
import services.SchemeService

object UserAddressPostALFNavigation {

  def navigate(submission: Submission, mode: Mode): Call =
    if (mode == NormalMode) {
      navigateInNormalMode(submission)
    } else {
      navigateInCheckMode()
    }

  private def navigateInNormalMode(submission: Submission): Call = {
    val firstPstr: PSTR = PSTR(
      SchemeService.allPensionSchemeDetails(submission.calculationInputs).head.pensionSchemeTaxReference
    )
    controllers.routes.LegacyPensionSchemeReferenceController.onPageLoad(NormalMode, firstPstr)
  }

  private def navigateInCheckMode(): Call =
    controllers.routes.CheckYourAnswersController.onPageLoad

}
