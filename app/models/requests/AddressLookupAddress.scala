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

package models.requests

import play.api.libs.functional.syntax.*
import play.api.libs.json.{Format, __}

case class AddressLookupAddress(
  organisation: Option[String],
  lines: List[String],
  postcode: Option[String],
  country: AddressLookupCountry
)

object AddressLookupAddress {
  implicit lazy val format: Format[AddressLookupAddress] = (
    (__ \ "organisation").formatNullable[String] and
      (__ \ "lines").format[List[String]] and
      (__ \ "postcode").formatNullable[String] and
      (__ \ "country").format[AddressLookupCountry]
  )(AddressLookupAddress.apply, o => Tuple.fromProductTyped(o))
}

case class AddressLookupCountry(code: String, name: String)

object AddressLookupCountry {
  implicit lazy val format: Format[AddressLookupCountry] = (
    (__ \ "code").format[String] and
      (__ \ "name").format[String]
  )(AddressLookupCountry.apply, o => Tuple.fromProductTyped(o))
}
