/*
 * Copyright 2025 HM Revenue & Customs
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

package models.bavf

import models.Enumerable
import play.api.libs.json.{Json, OFormat}

case class BavfCompleteResponse(
  accountType: String,
  personal: Option[BavfPersonalCompleteResponse],
  business: Option[BavfBusinessCompleteResponse]
)

object BavfCompleteResponse {
  implicit val bavfCompleteResponse: OFormat[BavfCompleteResponse] = Json.format[BavfCompleteResponse]
}

case class BavfCompleteResponseAddress(lines: List[String], town: Option[String], postcode: Option[String]) {}

object BavfCompleteResponseAddress {
  implicit val bavfCompleteResponseAddress: OFormat[BavfCompleteResponseAddress] =
    Json.format[BavfCompleteResponseAddress]
}

case class BavfExtendedCompleteResponse(bavfCompleteResponse: BavfCompleteResponse, extraInformation: Option[String])

object BavfExtendedCompleteResponse {
  implicit val bavfExtendedCompleteResponse: OFormat[BavfExtendedCompleteResponse] =
    Json.format[BavfExtendedCompleteResponse]
}
case class BavfPersonalCompleteResponse(
  address: Option[BavfCompleteResponseAddress],
  accountName: String,
  sortCode: String,
  accountNumber: String,
  accountNumberIsWellFormatted: ReputationResponseEnum,
  rollNumber: Option[String],
  accountExists: Option[ReputationResponseEnum],
  nameMatches: Option[ReputationResponseEnum],
  matchedAccountName: Option[String],
  nonStandardAccountDetailsRequiredForBacs: Option[ReputationResponseEnum],
  sortCodeBankName: Option[String],
  sortCodeSupportsDirectDebit: Option[ReputationResponseEnum],
  sortCodeSupportsDirectCredit: Option[ReputationResponseEnum],
  iban: Option[String]
)

object BavfPersonalCompleteResponse {
  implicit val bavfPersonalCompleteResponse: OFormat[BavfPersonalCompleteResponse] =
    Json.format[BavfPersonalCompleteResponse]
}

case class BavfBusinessCompleteResponse(
  address: Option[BavfCompleteResponseAddress],
  companyName: String,
  sortCode: String,
  accountNumber: String,
  rollNumber: Option[String],
  accountNumberIsWellFormatted: ReputationResponseEnum,
  accountExists: Option[ReputationResponseEnum],
  nameMatches: Option[ReputationResponseEnum],
  matchedAccountName: Option[String],
  nonStandardAccountDetailsRequiredForBacs: Option[ReputationResponseEnum],
  sortCodeBankName: Option[String],
  sortCodeSupportsDirectDebit: Option[ReputationResponseEnum],
  sortCodeSupportsDirectCredit: Option[ReputationResponseEnum],
  iban: Option[String]
)

object BavfBusinessCompleteResponse {
  implicit val bavfBusinessCompleteResponse: OFormat[BavfBusinessCompleteResponse] =
    Json.format[BavfBusinessCompleteResponse]
}

sealed trait ReputationResponseEnum

object ReputationResponseEnum extends Enumerable.Implicits {

  case object Yes extends WithName("yes") with ReputationResponseEnum

  case object No extends WithName("no") with ReputationResponseEnum

  case object Partial extends WithName("partial") with ReputationResponseEnum

  case object Indeterminate extends WithName("indeterminate") with ReputationResponseEnum

  case object Inapplicable extends WithName("inapplicable") with ReputationResponseEnum

  case object Error extends WithName("error") with ReputationResponseEnum

  val values: Seq[ReputationResponseEnum] =
    Seq(Yes, No, Partial, Indeterminate, Inapplicable, Error)

  implicit val enumerable: Enumerable[ReputationResponseEnum] =
    Enumerable(values.map(v => v.toString -> v): _*)
}

class WithName(string: String) {
  override val toString: String = string
}
