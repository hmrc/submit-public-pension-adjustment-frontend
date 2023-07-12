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

  implicit lazy val arbitraryUkAddressPage: Arbitrary[UkAddressPage.type] =
    Arbitrary(UkAddressPage)

  implicit lazy val arbitraryInternationalAddressPage: Arbitrary[InternationalAddressPage.type] =
    Arbitrary(InternationalAddressPage)

  implicit lazy val arbitraryAreYouAUKResidentPage: Arbitrary[AreYouAUKResidentPage.type] =
    Arbitrary(AreYouAUKResidentPage)
  implicit lazy val arbitraryStatusOfUserPage: Arbitrary[StatusOfUserPage.type] =
    Arbitrary(StatusOfUserPage)

  implicit lazy val arbitraryClaimOnBehalfPage: Arbitrary[ClaimOnBehalfPage.type] =
    Arbitrary(ClaimOnBehalfPage)
}
