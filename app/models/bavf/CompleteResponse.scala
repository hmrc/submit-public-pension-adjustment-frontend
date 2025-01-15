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
import play.api.libs.json.{Json, Reads, Writes}

case class CompleteResponse(
  accountType: String,
  personal: Option[PersonalCompleteResponse],
  business: Option[BusinessCompleteResponse]
)

case class CompleteResponseAddress(lines: List[String], town: Option[String], postcode: Option[String]) {
  override def toString: String =
    (lines ++ Seq(town, postcode).flatten).mkString("<br>")
}

object CompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress]   = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress] = Json.writes[CompleteResponseAddress]

  implicit val reads: Reads[CompleteResponse]   = Json.reads[CompleteResponse]
  implicit val writes: Writes[CompleteResponse] = Json.writes[CompleteResponse]
}

case class ExtendedCompleteResponse(completeResponse: CompleteResponse, extraInformation: Option[String])

object ExtendewdCompleteResponse {

  implicit val reads: Reads[ExtendedCompleteResponse]   = Json.reads[ExtendedCompleteResponse]
  implicit val writes: Writes[ExtendedCompleteResponse] = Json.writes[ExtendedCompleteResponse]
}

case class PersonalCompleteResponse(
  address: Option[CompleteResponseAddress],
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

object PersonalCompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress]   = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress] = Json.writes[CompleteResponseAddress]
  implicit val reads: Reads[PersonalCompleteResponse]         = Json.reads[PersonalCompleteResponse]
  implicit val writes: Writes[PersonalCompleteResponse]       = Json.writes[PersonalCompleteResponse]
}

case class BusinessCompleteResponse(
  address: Option[CompleteResponseAddress],
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

object BusinessCompleteResponse {
  implicit val addressReads: Reads[CompleteResponseAddress]             = Json.reads[CompleteResponseAddress]
  implicit val addressWrites: Writes[CompleteResponseAddress]           = Json.writes[CompleteResponseAddress]
  implicit val completeResponseReads: Reads[BusinessCompleteResponse]   = Json.reads[BusinessCompleteResponse]
  implicit val completeResponseWrites: Writes[BusinessCompleteResponse] = Json.writes[BusinessCompleteResponse]
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
