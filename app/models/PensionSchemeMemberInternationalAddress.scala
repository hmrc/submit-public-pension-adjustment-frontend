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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class PensionSchemeMemberInternationalAddress(
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  townOrCity: String,
  stateOrRegion: Option[String],
  postCode: Option[String]
)

object PensionSchemeMemberInternationalAddress {
  implicit val format: Format[PensionSchemeMemberInternationalAddress] = (
    (__ \ "addressLine1").format[String] and
      (__ \ "addressLine2").formatNullable[String] and
      (__ \ "addressLine3").formatNullable[String] and
      (__ \ "townOrCity").format[String] and
      (__ \ "stateOrRegion").formatNullable[String] and
      (__ \ "postCode").formatNullable[String]
  )(PensionSchemeMemberInternationalAddress.apply, o => Tuple.fromProductTyped(o))

}
