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

import models.StatusOfUser.{Deputyship, LegalPersonalRepresentative, PowerOfAttorney}
import models.{CheckMode, NormalMode, StatusOfUser}

import java.time.LocalDate

class StatusOfUserSpec extends PageBehaviours {

  "StatusOfUserPage" - {

    beRetrievable[StatusOfUser](StatusOfUserPage)

    beSettable[StatusOfUser](StatusOfUserPage)

    beRemovable[StatusOfUser](StatusOfUserPage)
  }

  "must redirect to their name page when user submits data" in {

    val page = StatusOfUserPage

    val userAnswers = emptyUserAnswers
      .set(page, Deputyship)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/name-someone-else")
  }

  "must redirect to their CYA page when status of user page is PowerOfAttorney in check mode" in {

    val page = StatusOfUserPage

    val userAnswers = emptyUserAnswers
      .set(page, PowerOfAttorney)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }

  "must redirect to their name page when status of user page is not PowerOfAttorney in check mode" in {

    val page = StatusOfUserPage

    val userAnswers = emptyUserAnswers
      .set(page, Deputyship)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-name-someone-else")
  }

  "must redirect to JourneyRecoveryPage when not answered in normal mode" in {

    val page = StatusOfUserPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to JourneyRecoveryPage when not answered in check mode" in {

    val page = StatusOfUserPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  val validDate = LocalDate.of(1995, 1, 1)

  "must cleanup member date of death when user selects PoA" in {

    val ua = emptyUserAnswers
      .set(MemberDateOfDeathPage, validDate)
      .success
      .value

    val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(PowerOfAttorney), ua).success.value

    cleanedUserAnswers.get(MemberDateOfDeathPage) mustBe None

  }

  "must not cleanup member date of death when user selects LegalPersonalRepresentative" in {

    val ua = emptyUserAnswers
      .set(MemberDateOfDeathPage, validDate)
      .success
      .value

    val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(LegalPersonalRepresentative), ua).success.value

    cleanedUserAnswers.get(MemberDateOfDeathPage) mustBe Some(validDate)

  }

  "must cleanup not member date of death when user selects deputyship" in {

    val ua = emptyUserAnswers
      .set(MemberDateOfDeathPage, validDate)
      .success
      .value

    val cleanedUserAnswers = StatusOfUserPage.cleanup(Some(Deputyship), ua).success.value

    cleanedUserAnswers.get(MemberDateOfDeathPage) mustBe Some(validDate)

  }
}
