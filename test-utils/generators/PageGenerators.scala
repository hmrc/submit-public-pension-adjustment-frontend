/*
 * Copyright 2023 HM Revenue & Customs
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

package generators

import org.scalacheck.Arbitrary
import pages._

trait PageGenerators {

  implicit lazy val arbitraryWhenWillYouAskPensionSchemeToPayPage
    : Arbitrary[WhenWillYouAskPensionSchemeToPayPage.type] =
    Arbitrary(WhenWillYouAskPensionSchemeToPayPage)

  implicit lazy val arbitraryWhenDidYouAskPensionSchemeToPayPage: Arbitrary[WhenDidYouAskPensionSchemeToPayPage.type] =
    Arbitrary(WhenDidYouAskPensionSchemeToPayPage)

  implicit lazy val arbitraryAskedPensionSchemeToPayTaxChargePage
    : Arbitrary[AskedPensionSchemeToPayTaxChargePage.type] =
    Arbitrary(AskedPensionSchemeToPayTaxChargePage)

  implicit lazy val arbitraryStatusOfUserPage: Arbitrary[StatusOfUserPage.type] =
    Arbitrary(StatusOfUserPage)

  implicit lazy val arbitraryClaimOnBehalfPage: Arbitrary[ClaimOnBehalfPage.type] =
    Arbitrary(ClaimOnBehalfPage)

  implicit lazy val arbitraryWhichPensionSchemeWillPayPage: Arbitrary[WhichPensionSchemeWillPayPage.type] =
    Arbitrary(WhichPensionSchemeWillPayPage)

  implicit lazy val arbitraryPensionSchemeDetailsPage: Arbitrary[PensionSchemeDetailsPage.type] =
    Arbitrary(PensionSchemeDetailsPage)

  implicit lazy val arbitraryWhoWillPayPage: Arbitrary[WhoWillPayPage.type] =
    Arbitrary(WhoWillPayPage)
}
