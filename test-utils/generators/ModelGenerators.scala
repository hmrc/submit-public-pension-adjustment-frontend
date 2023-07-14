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
import uk.gov.hmrc.domain.Nino

trait ModelGenerators {

  def ukPostcode: Gen[String] =
    for {
      firstChars <- Gen.choose(1, 2)
      first      <- Gen.listOfN(firstChars, Gen.alphaUpperChar).map(_.mkString)
      second     <- Gen.numChar.map(_.toString)
      third      <- Gen.oneOf(Gen.alphaUpperChar, Gen.numChar).map(_.toString)
      fourth     <- Gen.numChar.map(_.toString)
      fifth      <- Gen.listOfN(2, Gen.alphaUpperChar).map(_.mkString)
    } yield s"$first$second$third$fourth$fifth"

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
        pensionSchemeName         <- arbitrary[String]
        pensionSchemeTaxReference <- arbitrary[String]
      } yield PensionSchemeDetails(pensionSchemeName, pensionSchemeTaxReference)
    }

  implicit lazy val arbitraryWhoWillPay: Arbitrary[WhoWillPay] =
    Arbitrary {
      Gen.oneOf(WhoWillPay.values.toSeq)
    }

  implicit lazy val arbitraryWhichPensionSchemeWillPayTaxRelief: Arbitrary[WhichPensionSchemeWillPayTaxRelief] =
    Arbitrary {
      Gen.oneOf(WhichPensionSchemeWillPayTaxRelief.values.toSeq)
    }

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

  implicit lazy val arbitraryNino: Arbitrary[Nino] = Arbitrary {
    for {
      firstChar  <- Gen.oneOf('A', 'C', 'E', 'H', 'J', 'L', 'M', 'O', 'P', 'R', 'S', 'W', 'X', 'Y')
      secondChar <-
        Gen.oneOf('A', 'B', 'C', 'E', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z')
      digits     <- Gen.listOfN(6, Gen.numChar)
      lastChar   <- Gen.oneOf('A', 'B', 'C', 'D')
    } yield Nino(firstChar.toString + secondChar.toString + digits.mkString + lastChar.toString)
  }

  implicit lazy val arbitraryPensionSchemeMemberUKAddress: Arbitrary[PensionSchemeMemberUKAddress] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[Option[String]]
        townOrCity   <- arbitrary[String]
        county       <- arbitrary[Option[String]]
        postCode     <- arbitrary[String]
      } yield PensionSchemeMemberUKAddress(addressLine1, addressLine2, townOrCity, county, postCode)
    }

  implicit lazy val arbitraryPensionSchemeMemberInternationalAddress
    : Arbitrary[PensionSchemeMemberInternationalAddress] =
    Arbitrary {
      for {
        addressLine1 <- arbitrary[String]
        addressLine2 <- arbitrary[Option[String]]
        townOrCity   <- arbitrary[String]
        county       <- arbitrary[Option[String]]
        postCode     <- arbitrary[Option[String]]
        country      <- arbitrary[String]
      } yield PensionSchemeMemberInternationalAddress(addressLine1, addressLine2, townOrCity, county, postCode, country)
    }
}
