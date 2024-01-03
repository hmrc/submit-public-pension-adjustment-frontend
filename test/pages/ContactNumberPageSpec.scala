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

import models.{CheckMode, NormalMode}

class ContactNumberPageSpec extends PageBehaviours {

  "ContactNumberPage" - {

    beRetrievable[String](ContactNumberPage)

    beSettable[String](ContactNumberPage)

    beRemovable[String](ContactNumberPage)
  }

  "must redirect to AreYouAUKResidentPage page when user submits answers in normal mode" in {

    val page = ContactNumberPage

    val userAnswers = emptyUserAnswers
      .set(page, "0123456789")
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/uk-resident")
  }

  "must redirect to AreYouAUKResidentPage page when user does not enter an answer in normal mode" in {

    val page = ContactNumberPage

    val userAnswers = emptyUserAnswers
      .set(page, "")
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/uk-resident")
  }

  "must redirect to CYA page when user submits answers in check mode" in {

    val page = ContactNumberPage

    val userAnswers = emptyUserAnswers
      .set(page, "0123456789")
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = ContactNumberPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = ContactNumberPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

}
