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

import models.bavf.BavfCompleteResponse
import play.api.libs.json.{Json, OFormat}

case class BankDetails(accountName: String, sortCode: String, accountNumber: String, rollNumber: Option[String])

object BankDetails {
  implicit val format: OFormat[BankDetails] = Json.format[BankDetails]

  def apply(completeResponse: BavfCompleteResponse): BankDetails = {
    val personal = completeResponse.personal.get
    new BankDetails(
      accountName = personal.accountName,
      sortCode = personal.sortCode,
      accountNumber = personal.accountNumber,
      rollNumber = personal.rollNumber
    )
  }

}
