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

package models

import base.SpecBase
import models.requests.{AddressLookupAddress, AddressLookupConfirmation, AddressLookupCountry}
import org.scalatest.matchers.must.Matchers

class UkAddressSpec extends SpecBase with Matchers {

  "UkAddress" - {

    def addressLookupConfirmation(lines: List[String]) =
      AddressLookupConfirmation(
        auditRef = "ref",
        id = Some("id"),
        address = AddressLookupAddress(
          organisation = None,
          lines = lines,
          postcode = Some("postcode"),
          country = AddressLookupCountry("GB", "United Kingdom")
        )
      )

    "extract address lines" - {

      "four address lines are returned" in {
        val confirmation = addressLookupConfirmation(List("line1", "line2", "line3", "line4"))
        UkAddress.apply(confirmation) `mustBe` UkAddress(
          None,
          "line1",
          Some("line2"),
          Some("line3"),
          "line4",
          None,
          Some("postcode"),
          Some("United Kingdom")
        )
      }

      "three address lines are returned" in {
        val confirmation = addressLookupConfirmation(List("line1", "line2", "line3"))
        UkAddress
          .apply(confirmation) `mustBe` UkAddress(
          None,
          "line1",
          Some("line2"),
          None,
          "line3",
          None,
          Some("postcode"),
          Some("United Kingdom")
        )
      }

      "two address lines are returned" in {
        val confirmation = addressLookupConfirmation(List("line1", "line2"))
        UkAddress.apply(confirmation) `mustBe` UkAddress(
          None,
          "line1",
          None,
          None,
          "line2",
          None,
          Some("postcode"),
          Some("United Kingdom")
        )
      }
    }
  }
}
