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

import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryWhenWillYouAskPensionSchemeToPay: Arbitrary[WhenWillYouAskPensionSchemeToPay] =
    Arbitrary {
      Gen.oneOf(WhenWillYouAskPensionSchemeToPay.values.toSeq)
    }

  implicit lazy val arbitraryStatusOfUser: Arbitrary[StatusOfUser] =
    Arbitrary {
      Gen.oneOf(StatusOfUser.values.toSeq)
    }

  implicit lazy val arbitraryWhichPensionSchemeWillPay: Arbitrary[WhichPensionSchemeWillPay] =
    Arbitrary {
      Gen.oneOf(WhichPensionSchemeWillPay.values.toSeq)
    }

  implicit lazy val arbitraryPensionSchemeDetails: Arbitrary[PensionSchemeDetails] =
    Arbitrary {
      for {
        pensionSchemeName <- arbitrary[String]
        pensionSchemeTaxReference <- arbitrary[String]
      } yield PensionSchemeDetails(pensionSchemeName, pensionSchemeTaxReference)
    }

  implicit lazy val arbitraryWhoWillPay: Arbitrary[WhoWillPay] =
    Arbitrary {
      Gen.oneOf(WhoWillPay.values.toSeq)
    }
}
