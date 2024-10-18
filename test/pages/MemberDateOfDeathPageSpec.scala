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

import models.{CheckMode, NormalMode, RunThroughOnBehalfFlow}

import java.time.LocalDate
import org.scalacheck.Arbitrary

class MemberDateOfDeathPageSpec extends PageBehaviours {

  "MemberDateOfDeathPage" - {

    implicit lazy val arbitraryLocalDate: Arbitrary[LocalDate] = Arbitrary {
      datesBetween(LocalDate.of(1900, 1, 1), LocalDate.of(2100, 1, 1))
    }

    beRetrievable[LocalDate](MemberDateOfDeathPage)

    beSettable[LocalDate](MemberDateOfDeathPage)

    beRemovable[LocalDate](MemberDateOfDeathPage)
  }

  "must redirect to Pension Scheme Members Nino page when user submits data in normal mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .success
      .value

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/national-insurance-number-someone-else")

  }

  "must redirect to journey recovery when no data submitted in normal mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(NormalMode, userAnswers).url

    checkNavigation(nextPageUrl, "/there-is-a-problem")
  }

  "must redirect to Pension Scheme Members Nino page when user submits data in checkmode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers
      .set(page, LocalDate.of(1995, 1, 1))
      .get
      .set(RunThroughOnBehalfFlow(), true)
      .success
      .value

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/submission-service/change-national-insurance-number-someone-else")

  }

  "must redirect to check your answers when RunThroughOnBehalfFlow is false or empty in check mode" in {

    val page = MemberDateOfDeathPage

    val userAnswers = emptyUserAnswers

    val nextPageUrl: String = page.navigate(CheckMode, userAnswers).url

    checkNavigation(nextPageUrl, "/check-your-answers")
  }
}
