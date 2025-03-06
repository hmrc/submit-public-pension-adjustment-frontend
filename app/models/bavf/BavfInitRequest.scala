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

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsObject, OFormat, __}

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
  implicit val bavfInitRequest: OFormat[BavfInitRequest] = (
    (__ \ "serviceIdentifier").format[String] and
      (__ \ "continueUrl").format[String] and
      (__ \ "messages").formatNullable[BavfInitRequestMessages] and
      (__ \ "customisationsUrl").formatNullable[String] and
      (__ \ "prepopulatedData").formatNullable[InitRequestPrepopulatedData] and
      (__ \ "address").formatNullable[BavfInitRequestAddress] and
      (__ \ "timeoutConfig").formatNullable[InitRequestTimeoutConfig] and
      (__ \ "signOutUrl").formatNullable[String] and
      (__ \ "maxCallCount").formatNullable[Int] and
      (__ \ "maxCallCountRedirectUrl").formatNullable[String]
  )(BavfInitRequest.apply, o => Tuple.fromProductTyped(o))
}

final case class BavfInitRequestMessages(en: JsObject, cy: Option[JsObject] = None)

object BavfInitRequestMessages {
  implicit val bavfInitRequestMessages: OFormat[BavfInitRequestMessages] = (
    (__ \ "en").format[JsObject] and
      (__ \ "cy").formatNullable[JsObject]
  )(BavfInitRequestMessages.apply, o => Tuple.fromProductTyped(o))
}

final case class BavfInitRequestAddress(lines: List[String], town: Option[String], postcode: Option[String])

object BavfInitRequestAddress {
  implicit val bavfInitRequestAddress: OFormat[BavfInitRequestAddress] = (
    (__ \ "lines").format[List[String]] and
      (__ \ "town").formatNullable[String] and
      (__ \ "postcode").formatNullable[String]
  )(BavfInitRequestAddress.apply, o => Tuple.fromProductTyped(o))
}

final case class InitRequestTimeoutConfig(timeoutUrl: String, timeoutAmount: Int, timeoutKeepAliveUrl: Option[String])

object InitRequestTimeoutConfig {
  implicit val initRequestTimeoutConfig: OFormat[InitRequestTimeoutConfig] = (
    (__ \ "timeoutUrl").format[String] and
      (__ \ "timeoutAmount").format[Int] and
      (__ \ "timeoutKeepAliveUrl").formatNullable[String]
  )(InitRequestTimeoutConfig.apply, o => Tuple.fromProductTyped(o))
}

final case class InitRequestPrepopulatedData(
  accountType: Option[String] = None,
  name: Option[String] = None,
  sortCode: Option[String] = None,
  accountNumber: Option[String] = None,
  rollNumber: Option[String] = None
)

object InitRequestPrepopulatedData {
  implicit val initRequestPrepopulatedData: OFormat[InitRequestPrepopulatedData] = (
    (__ \ "accountType").formatNullable[String] and
      (__ \ "name").formatNullable[String] and
      (__ \ "sortCode").formatNullable[String] and
      (__ \ "accountNumber").formatNullable[String] and
      (__ \ "rollNumber").formatNullable[String]
  )(InitRequestPrepopulatedData.apply, o => Tuple.fromProductTyped(o))
}
