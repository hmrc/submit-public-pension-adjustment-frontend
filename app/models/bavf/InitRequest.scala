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

final case class InitRequest(
  serviceIdentifier: String,
  continueUrl: String,
  messages: Option[InitRequestMessages] = None,
  customisationsUrl: Option[String] = None,
  prepopulatedData: Option[InitRequestPrepopulatedData] = None,
  address: Option[InitRequestAddress] = None,
  timeoutConfig: Option[InitRequestTimeoutConfig] = None,
  signOutUrl: Option[String] = None,
  maxCallCount: Option[Int] = None,
  maxCallCountRedirectUrl: Option[String] = None
)

object InitRequest {
  implicit val initRequestFormat: OFormat[InitRequest] = Json.format[InitRequest]
}

final case class InitRequestMessages(en: JsObject, cy: Option[JsObject] = None)

object InitRequestMessages {
  implicit val initRequestMessages: OFormat[InitRequestMessages] = Json.format[InitRequestMessages]
}

final case class InitRequestAddress(lines: List[String], town: Option[String], postcode: Option[String])

object InitRequestAddress {
  implicit val initRequestAddress: OFormat[InitRequestAddress] = Json.format[InitRequestAddress]
}

final case class InitRequestTimeoutConfig(timeoutUrl: String, timeoutAmount: Int, timeoutKeepAliveUrl: Option[String])

object InitRequestTimeoutConfig {
  implicit val initRequestTimeoutConfig: OFormat[InitRequestTimeoutConfig] = Json.format[InitRequestTimeoutConfig]
}

case class InitResponse(journeyId: String, startUrl: String, completeUrl: String, detailsUrl: Option[String])

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

object InitResponse {
  implicit val initResponse: OFormat[InitResponse] = Json.format[InitResponse]
}
