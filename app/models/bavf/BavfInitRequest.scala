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

import play.api.libs.json.{JsObject, Json, OFormat}

final case class BavfInitRequest(
  serviceIdentifier: String,
  continueUrl: String,
  messages: Option[BavfInitRequestMessages] = None,
  customisationsUrl: Option[String] = None,
  prepopulatedData: Option[InitRequestPrepopulatedData] = None,
  address: Option[BavfInitRequestAddress] = None,
  timeoutConfig: Option[InitRequestTimeoutConfig] = None,
  signOutUrl: Option[String] = None,
  maxCallCount: Option[Int] = None,
  maxCallCountRedirectUrl: Option[String] = None
)

object BavfInitRequest {
  implicit val bavfInitRequest: OFormat[BavfInitRequest] = Json.format[BavfInitRequest]
}

final case class BavfInitRequestMessages(en: JsObject, cy: Option[JsObject] = None)

object BavfInitRequestMessages {
  implicit val bavfInitRequestMessages: OFormat[BavfInitRequestMessages] = Json.format[BavfInitRequestMessages]
}

final case class BavfInitRequestAddress(lines: List[String], town: Option[String], postcode: Option[String])

object BavfInitRequestAddress {
  implicit val bavfInitRequestAddress: OFormat[BavfInitRequestAddress] = Json.format[BavfInitRequestAddress]
}

final case class InitRequestTimeoutConfig(timeoutUrl: String, timeoutAmount: Int, timeoutKeepAliveUrl: Option[String])

object InitRequestTimeoutConfig {
  implicit val initRequestTimeoutConfig: OFormat[InitRequestTimeoutConfig] = Json.format[InitRequestTimeoutConfig]
}

final case class InitRequestPrepopulatedData(
  accountType: Option[String] = None,
  name: Option[String] = None,
  sortCode: Option[String] = None,
  accountNumber: Option[String] = None,
  rollNumber: Option[String] = None
)

object InitRequestPrepopulatedData {
  implicit val initRequestPrepopulatedData: OFormat[InitRequestPrepopulatedData] =
    Json.format[InitRequestPrepopulatedData]
}
