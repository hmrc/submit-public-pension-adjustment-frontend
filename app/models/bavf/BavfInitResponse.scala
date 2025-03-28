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
import play.api.libs.json.{OFormat, __}

case class BavfInitResponse(journeyId: String, startUrl: String, completeUrl: String, detailsUrl: Option[String])

object BavfInitResponse {
  implicit val bavfInitResponse: OFormat[BavfInitResponse] = (
    (__ \ "journeyId").format[String] and
      (__ \ "startUrl").format[String] and
      (__ \ "completeUrl").format[String] and
      (__ \ "detailsUrl").formatNullable[String]
  )(BavfInitResponse.apply, o => Tuple.fromProductTyped(o))
}
