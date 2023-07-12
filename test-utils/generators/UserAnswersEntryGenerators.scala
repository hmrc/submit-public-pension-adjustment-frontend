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
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryWhenWillYouAskPensionSchemeToPayUserAnswersEntry
    : Arbitrary[(WhenWillYouAskPensionSchemeToPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenWillYouAskPensionSchemeToPayPage.type]
        value <- arbitrary[WhenWillYouAskPensionSchemeToPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhenDidYouAskPensionSchemeToPayUserAnswersEntry
    : Arbitrary[(WhenDidYouAskPensionSchemeToPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhenDidYouAskPensionSchemeToPayPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAskedPensionSchemeToPayTaxChargeUserAnswersEntry
    : Arbitrary[(AskedPensionSchemeToPayTaxChargePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AskedPensionSchemeToPayTaxChargePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryStatusOfUserUserAnswersEntry: Arbitrary[(StatusOfUserPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[StatusOfUserPage.type]
        value <- arbitrary[StatusOfUser].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryClaimOnBehalfUserAnswersEntry: Arbitrary[(ClaimOnBehalfPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[ClaimOnBehalfPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhichPensionSchemeWillPayUserAnswersEntry
    : Arbitrary[(WhichPensionSchemeWillPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhichPensionSchemeWillPayPage.type]
        value <- arbitrary[WhichPensionSchemeWillPay].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryPensionSchemeDetailsUserAnswersEntry: Arbitrary[(PensionSchemeDetailsPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[PensionSchemeDetailsPage.type]
        value <- arbitrary[PensionSchemeDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWhoWillPayUserAnswersEntry: Arbitrary[(WhoWillPayPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WhoWillPayPage.type]
        value <- arbitrary[WhoWillPay].map(Json.toJson(_))
      } yield (page, value)
    }
}
