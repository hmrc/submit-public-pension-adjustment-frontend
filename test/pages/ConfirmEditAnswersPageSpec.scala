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

import models.NormalMode

class ConfirmEditAnswersPageSpec extends PageBehaviours {

  "ConfirmEditAnswersPage" - {

    beRetrievable[Boolean](ConfirmEditAnswersPage)

    beSettable[Boolean](ConfirmEditAnswersPage)

    beRemovable[Boolean](ConfirmEditAnswersPage)
  }

  "to edit calculation controller when yes" in {
    val ua = emptyUserAnswers
      .set(ConfirmEditAnswersPage, true)
      .get

    val nextPageUrl: String = ConfirmEditAnswersPage.navigate(NormalMode, ua).url

    checkNavigation(nextPageUrl, "/edit-calculation")
  }

  "to continue choice page when no" in {

    val ua = emptyUserAnswers
      .set(ConfirmEditAnswersPage, false)
      .get

    val nextPageUrl: String = ConfirmEditAnswersPage.navigate(NormalMode, ua).url

    checkNavigation(nextPageUrl, "/continue-choice")

  }

  "to journey recover when not answered" in {

    val nextPageUrl: String = ConfirmEditAnswersPage.navigate(NormalMode, emptyUserAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
