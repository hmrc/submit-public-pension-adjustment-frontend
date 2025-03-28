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
import play.api.libs.json.{Format, __}

import java.time.LocalDate

case class AuthRetrievals(
  userId: String,
  name: String,
  saUtr: Option[String],
  dob: Option[LocalDate]
)

object AuthRetrievals {

  implicit lazy val format: Format[AuthRetrievals] = (
    (__ \ "userId").format[String] and
      (__ \ "name").format[String] and
      (__ \ "saUtr").formatNullable[String] and
      (__ \ "dob").formatNullable[LocalDate]
  )(AuthRetrievals.apply, o => Tuple.fromProductTyped(o))
}
