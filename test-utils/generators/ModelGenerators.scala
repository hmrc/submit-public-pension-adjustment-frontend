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

  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[Option[String]]
        townOrCity   <- arbitrary[String]
        county       <- arbitrary[Option[String]]
        postCode     <- arbitrary[String]
      } yield UkAddress(addressLine1, addressLine2, townOrCity, county, postCode)
    }

  implicit lazy val arbitraryInternationalAddress: Arbitrary[InternationalAddress] =
    Arbitrary {
      for {
        addressLine1  <- arbitrary[String]
        addressLine2  <- arbitrary[Option[String]]
        townOrCity    <- arbitrary[String]
        stateOrRegion <- arbitrary[Option[String]]
        postCode      <- arbitrary[Option[String]]
        country       <- arbitrary[String]
      } yield InternationalAddress(addressLine1, addressLine2, townOrCity, stateOrRegion, postCode, country)
    }

  implicit lazy val arbitraryStatusOfUser: Arbitrary[StatusOfUser] =
    Arbitrary {
      Gen.oneOf(StatusOfUser.values.toSeq)
    }
}
