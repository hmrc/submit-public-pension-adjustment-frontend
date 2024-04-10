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

import models.{ContinueChoice, NavigationState, NormalMode}

class ContinueChoicePageSpec extends PageBehaviours {

  "ContinueChoicePage" - {

    beRetrievable[ContinueChoice](ContinueChoicePage)

    beSettable[ContinueChoice](ContinueChoicePage)

    beRemovable[ContinueChoice](ContinueChoicePage)
  }

  "Normal Mode" - {

    // TODO Change test to continue journey when implemented
    "must redirect to continue journey, when continue is selected" in {
      val ua = emptyUserAnswers
        .set(ContinueChoicePage, ContinueChoice.Edit)
        .get

      val nextPageUrl: String = ContinueChoicePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/submission-service/submitting-on-behalf-someone-else")
    }

    "must redirect to continue journey where user journey maintained in NavigationState" in {
      val ua = emptyUserAnswers
        .set(ContinueChoicePage, ContinueChoice.Continue)
        .get

      val answersWithNav = NavigationState.save(ua, "/submission-service/uk-resident")

      val nextPageUrl: String = ContinueChoicePage.navigate(NormalMode, answersWithNav).url.trim

      checkNavigation(nextPageUrl, "/continue-session")
    }

    "must redirect to journey recovery, when no answer on the page" in {
      val ua = emptyUserAnswers

      val nextPageUrl: String = ContinueChoicePage.navigate(NormalMode, ua).url

      checkNavigation(nextPageUrl, "/there-is-a-problem")
    }
  }
}
