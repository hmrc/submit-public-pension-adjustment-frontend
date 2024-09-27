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

import models.{CheckMode, NormalMode, StatusOfUser}
import org.scalacheck.Arbitrary

import java.time.LocalDate

class PensionSchemeMemberDOBPageSpec extends PageBehaviours {

  "PensionSchemeMemberDOBPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](PensionSchemeMemberDOBPage)

    beSettable[LocalDate](PensionSchemeMemberDOBPage)

    beRemovable[LocalDate](PensionSchemeMemberDOBPage)
  }

  "must redirect to Pension Scheme Members Nino page when user submits data and has selected power of attorney" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/national-insurance-number-someone-else")

  }

  "must redirect to Members Date of Death page when user submits data and has selected deputyship" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.Deputyship)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/date-of-death-someone-else")
  }

  "must redirect to Members Date of Death page when user submits data and has selected LegalPersonalRepresentative" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/date-of-death-someone-else")
  }

  "must redirect to journey recovery when no answer for status of user page in normal mode" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to Pension Scheme Members Nino page when user submits data in checkmode and has selected power of attorney" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.PowerOfAttorney)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-national-insurance-number-someone-else")
  }

  "must redirect to Members Date of Death page when user submits data in checkmode and has selected deputyship" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.Deputyship)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-date-of-death-someone-else")
  }

  "must redirect to Members Date of Death page when user submits data in checkmode and has selected LegalPersonalRepresentative" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value
      .set(StatusOfUserPage, StatusOfUser.LegalPersonalRepresentative)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-date-of-death-someone-else")
  }

  "must redirect to journey recovery when no answer for status of user page in check mode" in {

    val page = PensionSchemeMemberDOBPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }
}
