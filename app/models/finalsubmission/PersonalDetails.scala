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

package models.finalsubmission

import play.api.libs.functional.syntax.*
import models.{InternationalAddress, UkAddress}
import play.api.libs.json.{Format, __}

import java.time.LocalDate

case class PersonalDetails(
  fullName: String,
  alternateName: Option[String],
  dateOfBirth: Option[LocalDate],
  address: Option[UkAddress],
  internationalAddress: Option[InternationalAddress],
  pensionSchemeMemberAddress: Option[UkAddress],
  pensionSchemeMemberInternationalAddress: Option[InternationalAddress],
  contactPhoneNumber: Option[String]
) {}

object PersonalDetails {

  implicit lazy val formats: Format[PersonalDetails] = (
    (__ \ "fullName").format[String] and
      (__ \ "alternateName").formatNullable[String] and
      (__ \ "dateOfBirth").formatNullable[LocalDate] and
      (__ \ "address").formatNullable[UkAddress] and
      (__ \ "internationalAddress").formatNullable[InternationalAddress] and
      (__ \ "pensionSchemeMemberAddress").formatNullable[UkAddress] and
      (__ \ "pensionSchemeMemberInternationalAddress").formatNullable[InternationalAddress] and
      (__ \ "contactPhoneNumber").formatNullable[String]
  )(PersonalDetails.apply, o => Tuple.fromProductTyped(o))
}
