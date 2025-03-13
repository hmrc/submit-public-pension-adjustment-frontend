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

import models.requests.AddressLookupConfirmation
import play.api.libs.json.*

case class UkAddress(
  organisation: Option[String],
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  townOrCity: String,
  county: Option[String],
  postCode: Option[String],
  country: Option[String]
)

object UkAddress {
  implicit val format: OFormat[UkAddress] = Json.format[UkAddress]

  def apply(addressLookupConfirmation: AddressLookupConfirmation): UkAddress = {
    val lines = addressLookupConfirmation.extractAddressLines()
    new UkAddress(
      organisation = addressLookupConfirmation.address.organisation,
      addressLine1 = lines._1,
      addressLine2 = lines._2,
      addressLine3 = lines._3,
      townOrCity = lines._4,
      postCode = addressLookupConfirmation.address.postcode,
      county = None,
      country = Some(addressLookupConfirmation.address.country.name)
    )
  }
}
